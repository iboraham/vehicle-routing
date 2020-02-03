var add = document.getElementById("add");

var apiButton = document.getElementById("apiButton")

apiButton.onclick = function() {
    var api_key = document.getElementById("apiBox").value;
}

add.onclick= function() {
    var adress = document.getElementById("adress").value;
    adress.replace(" ", "%");
    adress.replace(",", "|");
    var origins = adress;
    var destinations = adress;
    var format="json";
    var mode = "driving";
    var language= "tr-TR";
    var url = "https://maps.googleapis.com/maps/api/distancematrix/"+format+"?origins="+origins+"&destinations="+destinations+"&mode="+mode+"&language="+language+"&key="+api_key;
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", url, true);

    xmlhttp.send();

    xmlhttp.onreadystatechange = function() {
        var myArr = JSON.parse(this.responseText);
        document.getElementById("rows").innerHTML = this.rows.value
    };
};



