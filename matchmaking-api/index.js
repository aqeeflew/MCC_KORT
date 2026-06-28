const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');

// Initialize Firebase Admin (Requires service account JSON)
// For local testing, ensure GOOGLE_APPLICATION_CREDENTIALS env variable is set
// or provide the service account object directly to admin.initializeApp()
admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  databaseURL: process.env.FIREBASE_DATABASE_URL || "https://kort-mcc-default-rtdb.asia-southeast1.firebasedatabase.app"
});

const db = admin.database();
const app = express();
app.use(cors());
app.use(express.json());

// Broadcast a need for players
app.post('/api/matchmaking/broadcast', async (req, res) => {
  try {
    const { bookingId, hostUserId, sport, court, neededPlayers, date, time } = req.body;
    
    if (!hostUserId || !sport || !neededPlayers || !date || !time) {
      return res.status(400).json({ error: 'Missing required fields' });
    }

    const matchRef = db.ref('matchmaking').push();
    const matchData = {
      matchId: matchRef.key,
      bookingId: bookingId || null,
      hostUserId,
      sport,
      court: court || "Any Court",
      neededPlayers: parseInt(neededPlayers),
      date,
      time,
      players: [hostUserId], // Host is the first player
      status: 'open',
      createdAt: admin.database.ServerValue.TIMESTAMP
    };

    await matchRef.set(matchData);
    res.status(201).json({ message: 'Broadcast successful', matchData });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Get available matches
app.get('/api/matchmaking/available', async (req, res) => {
  try {
    const snapshot = await db.ref('matchmaking')
      .orderByChild('status')
      .equalTo('open')
      .once('value');

    const matches = [];
    snapshot.forEach(child => {
      const match = child.val();
      if (match.neededPlayers > 0) {
        matches.push(match);
      }
    });

    res.status(200).json({ matches });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Join a match
app.post('/api/matchmaking/join', async (req, res) => {
  try {
    const { matchId, joinUserId } = req.body;

    if (!matchId || !joinUserId) {
      return res.status(400).json({ error: 'Missing matchId or joinUserId' });
    }

    const matchRef = db.ref(`matchmaking/${matchId}`);
    
    await matchRef.transaction((currentData) => {
      if (currentData === null) {
        return currentData; // Match doesn't exist
      }
      
      if (currentData.neededPlayers > 0) {
        if (!currentData.players) {
          currentData.players = [];
        }
        if (!currentData.players.includes(joinUserId)) {
          currentData.players.push(joinUserId);
          currentData.neededPlayers -= 1;
          
          if (currentData.neededPlayers === 0) {
            currentData.status = 'filled';
          }
        }
      }
      return currentData;
    });

    res.status(200).json({ message: 'Successfully joined match' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Matchmaking API running on port ${PORT}`);
});
