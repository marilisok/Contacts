function email() {
    "use strict";
    var checkboxes = document.getElementsByName('marked'), length = checkboxes.length;
    var table = document.getElementById("tab");
    var address= document.getElementById("address");
    var countAddress = 0;
    var addressesOnRow = 3;
    address.value = "";
    for (var i=0; i<length; i++) {
        if (checkboxes[i].checked) {
            var row = table.rows[i+1];
            var email = row.cells[6].childNodes[0].value;
            if (email) {
                var separator = ",";
                if(countAddress % addressesOnRow === 0) separator  += "\n";
                if(countAddress !== 0) address.value += separator;
                address.value += email;
                countAddress++;
            }
        }
    }
    if(countAddress !==0) {
        var addressRows = countAddress / addressesOnRow| 0;
        if (addressRows === 0 || countAddress % addressesOnRow !== 0) addressRows++;
        if(addressRows > 4) addressRows = 4;
        address.rows = addressRows;
        openbox('emailPopUp');
    } else {
        alert("Choose contacts with email, please");
        return false;
    }

}
function setTemplate() {
    "use strict";
    var text = document.getElementById("text");
    var template = document.getElementById('templateEmail');
    var k;
    for (var i = 0; i < template.options.length; i++) {
        if (template.options[i].selected===true){
            k=i;
            break;
        }
    }
    if(template.options[k].value  === "Without template") {
        text.value = "";
        return false;
    }
    getText(template.options[k].value);
}
function getText(template) {
    fetch('front?command=settemplate&template='+template).then(function(response) {
        if(response.ok) {
            response.json().then(function(json) {
                var text = document.getElementById("text");
                text.value = json;
                //return json;

            });
        } else {
            return "";
        }
    });
}
function sendEmail() {
        var formData = new FormData(document.getElementById('email'));
        var jsonEmail = {};

        for (const [key, value]  of formData.entries()) {
                jsonEmail[key] = value;
        }
        alert(JSON.stringify(jsonEmail))
        fetch('front?command=email', {
            method: 'POST',
            body: JSON.stringify(jsonEmail)
        }).then( function(response){
            return response.json();
        })


}