//------------------------------------------------------------//

function GeoLoc (inputId) {

   console.log("  linking geoloc...");

   this.inputId = inputId;
   this.geocoder = null;
   this.location = {};

   var me = this;

   this.tryToFindPosition();
}

//------------------------------------------------------------//

//init geocoder and geoloc
GeoLoc.prototype.tryToFindPosition = function() {
   this.geocoder = new google.maps.Geocoder();
   this.initAutocomplete();       
}

//------------------------------------------------------------//

//init autocomplete
GeoLoc.prototype.initAutocomplete = function(){
   var me = this;
   var input = document.getElementById(this.inputId);
   var options = {
//         componentRestrictions: {country: 'fr'},
         types: ['(cities)']
   };

   var autocomplete = new google.maps.places.Autocomplete(input,options);
   
   google.maps.event.addListener(autocomplete, 'place_changed', function(){ me.lookForLocation() });

   this.lookForLocation();
}

//------------------------------------------------------------//

//when button "go" is clicked or "autocomplete"
GeoLoc.prototype.lookForLocation = function() {
   var me = this;
   var address = document.getElementById(this.inputId).value;
   this.geocoder.geocode( { 'address': address}, function(results, status){ me.locationFound(results, status) } );
}

//when button "go" is clicked or "autocomplete"
//GeoLoc.prototype.setLocation = function(lat, lon) {
//   console.log("setLocation", lat,lon)
//   var latLng = new google.maps.LatLng(lat, lon);
//   var me = this;
//   this.geocoder.geocode( { 'latLng': latLng}, function(results, status){
//      console.log(results)
//      document.getElementById(me.inputId).value = results[1].formatted_address
//   });
//}


//------------------------------------------------------------//

//when ajax geocoder returns
GeoLoc.prototype.locationFound = function(results, status) {
   if (status == google.maps.GeocoderStatus.OK) {
      var lat = results[0].geometry.location.lat();    
      var lon = results[0].geometry.location.lng();

      this.location = {
            name : $("#"+this.inputId).val(),
            lat : lat,
            lon : lon,
      }
   }
}