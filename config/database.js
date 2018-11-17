if(process.env.NODE_ENV === 'production'){
  module.exports = {mongoURI: 'mongodb://Kollol:turnover121@ds259109.mlab.com:59109/gps'}
} else {
  module.exports = {mongoURI: 'mongodb://localhost/gps'}
}