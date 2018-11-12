const mongoose = require('mongoose');
require('mongoose-double')(mongoose);

var Schema = mongoose.Schema;
var SchemaTypes = mongoose.Schema.Types;

var geoLocation = new Schema ({
    place: {
        type: String,
        required: true
    },

    lat: {
        type: SchemaTypes.Double,
        require: true
    },

    lng: {
        type: SchemaTypes.Double,
        required: true
    }
});

module.exports = mongoose.model('location', geoLocation);