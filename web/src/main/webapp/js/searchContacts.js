var limit = 10;
function searchContacts(){
    var mainForm = document.getElementById("mainForm");
    mainForm.style.display = "none";
    var contact = {};
    var template = document.getElementById('template2').innerHTML;
    Mustache.parse(template);
    var rendered = Mustache.render(template, contact);
    document.getElementById('header').innerHTML = "<h1 class='page-header'>Поиск контактов</h1>";
    document.getElementById('target2').innerHTML = rendered;
    var avatar = document.getElementById('imageContact');
    avatar.style.display = "none";
    var footer = document.getElementById('paging');
    footer.style.display = 'none';
    var attachForm = document.getElementById('tabAttach');
    attachForm.style.display = 'none';
    var phonesForm = document.getElementById('tabPhone');
    phonesForm.style.display = 'none';
    var buttonsAttach = document.getElementById('buttonsAttach');
    buttonsAttach.style.display = 'none';
    var buttonsPhones = document.getElementById('buttonsPhones');
    buttonsPhones.style.display = 'none';
    var mainButtons = document.getElementById('mainButtons');
    mainButtons.style.display = 'none';
    var searchButton = document.getElementById('searchButtons');
    searchButton.style.display = 'block';
    var birthdayRange = document.getElementById('birthdayRange');
    birthdayRange.style.display = 'block';
    var birth = document.getElementById('birth1');
    birth.style.display = 'none';
    var website = document.getElementById('website1');
    website.style.display = 'none';
    var email = document.getElementById('email1');
    email.style.display = 'none';
    var company = document.getElementById('company1');
    company.style.display = 'none';
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'none';
    var name = document.getElementById('required');
    name.setAttribute('id', 'nameSearch')

    var surname = document.getElementsByName('requiredSurname')[0];
    surname.setAttribute('id', 'searchSurname')

    var city = document.getElementsByName('requiredCity')[0];
    city.setAttribute('id', 'searchCity')

    //search()
}
var jsonObject;
function nextSearch() {
    "use strict";
    var form = document.getElementById("paging1");
    var page = form.getAttribute('value');
    if(page != form.getAttribute('max'))
        getSearch(++page);
}
function prevSearch() {
    "use strict";
    var form = document.getElementById("paging1");
    var page = form.getAttribute('value');
    if(page != 1)
        getSearch(--page);
}
function findContact() {
    var myForm = document.getElementById("contactForm");
    event.preventDefault();
    var formData = new FormData(myForm);
    myForm.style.display='none';
    var header = document.getElementById('header');
    header.style.display = 'none';
    var jsonContact = {};

    var strSearch = "Search parametrs - ";
    for (const [key, value]  of formData.entries()) {
        jsonContact[key] = value;
        if(value && value!=="NONE"){
            strSearch+=key+": "+value+" ";
        }
    }

    jsonObject = {'contact': jsonContact};
    //alert(JSON.stringify(jsonObject))
    fetch('front?command=search&page='+1+'&limit='+limit, {
        method: 'POST',
        body: JSON.stringify(jsonObject)
    }).then( function(response) {
        if(response.ok) {


            response.json().then(function(json) {
                var contact = json['contacts'];
                if(json['count']===0){
                    strSearch+=". Contacts don't exist";
                    document.getElementById('header1').innerHTML = "<h3 class='page-header'>"+strSearch+"</h3>";
                    return;
                }
                var data = {contacts: contact};
                var template = document.getElementById('template').innerHTML;

                //Parse it (optional, only necessary if template is to be used again)
                Mustache.parse(template);


                //Render the data into the template
                var rendered = Mustache.render(template, data);

                document.getElementById('target').innerHTML = rendered;
                document.getElementById('header1').innerHTML = "<h3 class='page-header'>"+strSearch+"</h3>";
                pagingSearch(json['count']);

            });
        } else {
            alert("Что-то пошло не так");
        }
    }).catch(function (error) {
        console.log('There has been a problem with your fetch operation: ' + error.message);
    })
}
function search() {
    var myForm = document.getElementById("contactForm");

    myForm.addEventListener('submit', findContact())
}
function getSearch(page) {
    var serId = "s"+page;
    var a = document.getElementById(serId);
    var paging = document.getElementById('paging1');
    paging.setAttribute('value', page);
    fetch('front?command=search&page='+page+'&limit='+limit, {
        method: 'POST',
        body: JSON.stringify(jsonObject)
    }).then(function(response) {
        if(response.ok) {


            response.json().then(function(json) {
                var contact = json['contacts'];
                var data = {contacts: contact};
                var template = document.getElementById('template').innerHTML;


                Mustache.parse(template);


                var rendered = Mustache.render(template, data);
                document.getElementById('target').innerHTML = rendered;
            });
        } else {
            alert("Что-то пошло не так");
        }
    });
}
function pagingSearch(count) {
    if(count===0)
        return;
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'block';
    pagination.innerHTML="";


    var a = document.createElement('a');
    a.style.background = "powderblue"
    pagination.appendChild(a);
    a.innerHTML="<a href='javascript:{}' onclick='prevSearch()'><<</a>";
    pagination.setAttribute('max', count);
    pagination.setAttribute('limit', limit);
    for(var i=0; i<=count; i++){
        var a = document.createElement('a');
        a.style.background ="powderblue";
        pagination.appendChild(a);
        a.innerHTML="<a id='"+"s"+(i+1)+"'href='javascript:{}' onclick='getSearch("+(i+1)+")'>"+(i+1)+"</a>";
    }
    var a1 = document.createElement('a');
    pagination.appendChild(a);
    a.innerHTML="<a href='javascript:{}' onclick='nextSearch()'>>></a>"
    pagination.style.background = 'powderblue';
}