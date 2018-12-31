const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const path = require('path');

// Express Instance
const app = express();
const server = require('http').createServer(app);
const http = require('http').Server(app);
const io = require('socket.io')(http);

var markersId = [];
//const Request = require("request");


const axios = require("axios");
const port = process.env.PORT || 3000;

// Declaring Global lat lng variable
var lat = 0, lng = 0;

// create our router
const router = express.Router();

//DB Config
const db = require('./config/database');

// Map to global promise
mongoose.Promise = global.Promise;

// Connecting to MongoDB
mongoose.connect(db.mongoURI, {
    useNewUrlParser: true
})
    .then(() => console.log('Connected to MongoDB ...'))
    .catch(err => console.log(err));

// location models lives here
const Location = require('./models/location');



// io.use(function (socket, next) {
//     sessionData(socket.request, socket.request.res, next);
// });


// Configure body parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


// middleware to use for all requests
router.use(function (req, res, next) {
    // do logging
    console.log('Something is happening.');
    next();
});

// REGISTER OUR ROUTES -------------------------------
app.use('/', router);



io.on('connection', function (socket) {
    console.log('a user connected');

    socket.on('marker', (data) => {
        console.log('marker latitude: ' + data.lat + " longitude : " + data.lng);
        io.sockets.emit('show-marker', data);
    });


    socket.on('device-marker', (data) => {
        markersId[socket.id] = data;
        console.log('device latitude : ' + markersId[socket.id].lat + ' longitude: ' + markersId[socket.id].lng);
        io.sockets.emit('show-device-marker', {
            id: socket.id,
            lat: markersId[socket.id].lat,
            lng: markersId[socket.id].lng
        });
    });
    socket.on('disconnect', () => {
        console.log(`${socket.id} is disconnected`);
    });                                                             
});


//console.log(lat + " and " + lng);

// Configure body parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());



// // HTTP Post Request
// router.route('/api').post((req, res) => {
//     var location = new Location();
//     location.deviceId = req.body.deviceId;
//     location.place = req.body.place;
//     location.lat = req.body.lat;
//     location.lng = req.body.lng;

//     location.save((err) => {
//         if (err)
//             res.send(err);
//         else {
//             res.json(location);
//         }
//     });
// }).get((req, res) => {
//     Location.find((err, locations) => {
//         if (err)
//             console.log(err);
//         else {
//             res.json(locations);
//         }
//     })
// });

// Ref - https://github.com/SharifCoding/basic-crud-with-mongodb


//updating location marker 

// app.put('/api/:deviceId', (req, res, next) => {
//     let id = {
//       deviceid: ObjectID(req.params.deviceId)
//     };

//     dbase.collection("locations").update({place: req.body.place},{deviceid: deviceId}, {$set:{'place': req.body.place, 'lat': req.body.lat, 'lng': req.body.lng}}, (err, result) => {
//       if(err) {
//         throw err;
//       }
//       console.log("user updated successfully!");
//     });
// });

// // Retrieving Data from API
// Request.get('http://localhost:3000/api', (error, response, body) => {
//     if (error) {
//         return console.log(error);
//     }
//     console.log(JSON.parse(body));
//     let json = JSON.parse(body);
//     lat = json[0].lat || 0;
//     lng = json[0].lng || 0;

//     //console.log(lat + " and " + lng);

//  });

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname + '/index.html'));
});

// app.listen(port, () => {
//     console.log(`Server Connected at ${port}.`);
// });

http.listen(port, function () {
    console.log(`five minute catch up is on port ${port}`);
});

module.exports = server;
