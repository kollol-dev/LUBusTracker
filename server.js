const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const path = require('path');
const Request = require("request");
const port = process.env.PORT || 3000;

// Declaring Global lat lng variable
var lat = 0, lng = 0;

// create our router
const router = express.Router();

DB Config
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

// Express Instance
const app = express();

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



//console.log(lat + " and " + lng);

// Configure body parser
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());



// HTTP Post Request
router.route('/api').post((req, res) => {
    var location = new Location();
    location.place = req.body.place;
    location.lat = req.body.lat;
    location.lng = req.body.lng;

    location.save((err) => {
        if (err)
            res.send(err);
        else {
            res.json(location);
        }
    });
}).get((req, res) => {
    Location.find((err, locations) => {
        if (err)
            console.log(err);
        else {
            res.json(locations);
        }
    })
});

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

app.listen(port, () => {
    console.log(`Server Connected at ${port}.`);
});