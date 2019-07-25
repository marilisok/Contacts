function add(){
    var mainForm = document.getElementById("mainForm");
    mainForm.style.display = "none";
    var footer = document.getElementById('paging');
    footer.style.display = 'none';
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'none';
    var contact = {};
    var template = document.getElementById('template2').innerHTML;
    Mustache.parse(template);
    var rendered = Mustache.render(template, contact);
    document.getElementById('header').innerHTML = "<h1 class='page-header'>Создание контакта</h1>";
    document.getElementById('target2').innerHTML = rendered;
}
function editContact(id){
    var footer = document.getElementById('paging');
    footer.style.display = 'none';
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'none';
    var mainForm = document.getElementById("mainForm");
    mainForm.style.display = "none";
    fetch('front?command=get&id='+id).then(function(response){
        if(response.ok) {


            response.json().then(function(json) {


                var contact = json;

                var data = {contacts: contact};

                var template = document.getElementById('template2').innerHTML;

                //Parse it (optional, only necessary if template is to be used again)
                Mustache.parse(template);


                //Render the data into the template
                var rendered = Mustache.render(template, contact);

                //Overwrite the contents of #target with the rendered HTML
                document.getElementById('header').innerHTML = "<h1 class='page-header'>Редактирование контакта</h1>";

                document.getElementById('target2').innerHTML = rendered;

                var contactGender = document.getElementById('contactGender').value;
                var gender = document.getElementById('gender');
                for (var i = 0; i < gender.options.length; i++) {
                    if (gender.options[i].value == contactGender)
                        gender.options[i].selected = true;
                }



                var contactMaritalStatus = document.getElementById('contactMaritalStatus').value;
                var marital = document.getElementById('marital');
                for (var i = 0; i < marital.options.length; i++) {
                    if (marital.options[i].value == contactMaritalStatus)
                        marital.options[i].selected = true;
                }

            });
        } else {
            alert("Что-то пошло не так");
        }
    })
}
function editMenu() {
    var footer = document.getElementById('paging');
    footer.style.display = 'none';
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'none';
    var form= document.getElementById("mainForm");
    var table = document.getElementById("tab");
    var checkboxes = document.getElementsByName('marked'), length = checkboxes.length;

    for (var i=0; i<length; i++) {
        if (checkboxes[i].checked){
            var id = checkboxes[i].getAttribute('value');
            editContact(id);
            break;
        }
    }
    if(i===length){
        alert("Choose contact, please");
    }
}
function openbox(id) {
    "use strict";
    var div = document.getElementById(id);

    if(div.style.display === 'block') {
        div.style.display = 'none';
    }
    else {
        div.style.display = 'block';
    }
}
var arrDel=[];
var dataAdd=[];
var dataEdit=[];
var phoneAction={};
var k=1;
var phoneService = {
    pos: 0,
    popUp: 'phonePopUp',
    mode: 0,
    MINVALUE: 1000,
    MAXVALUE: 99999999,
    addPhone: function () {
        "use strict";
        this.mode = 0;
        openbox(this.popUp);
    },
    cancelPhone: function () {
        "use strict";
        document.getElementById("telephone").reset();
        openbox(this.popUp);
    },
    deletePhone:function () {

        var isTable = document.getElementById('tabPhone');
        var nBoxes = document.getElementsByName('phones');
        var i, length=nBoxes.length;
        for (i=length-1; i>=0; i--) {
            if (nBoxes[i].checked){
                var id =nBoxes[i].getAttribute("value");
                if(!id){
                    for (var j=0; j<dataAdd.length; j++){
                        var obj = dataAdd[j];
                        if(obj.timeId==nBoxes[i].getAttribute('id')){
                            dataAdd.splice(j, 1);
                            break;
                        }
                    }
                }else{
                    for (var j=0; j<dataEdit.length; j++){
                        var obj = dataEdit[j];
                        if(id === obj.phoneId){
                            dataEdit.splice(j, 1);
                            break;
                        }
                    }
                    arrDel.push(id);
                }
                isTable.deleteRow(i);


            }
        }
        alert(arrDel)
        phoneAction['del']=arrDel;

    },
    savePhone: function(){
        "use strict";
        var form= document.getElementById("telephone");
        if (!(form.operatorCode.value && form.phone.value )) {
            alert("Please, fill required fields");
            return false;
        }
        if (form.countryCode.value > this.MINVALUE  ||  form.phone.value > this.MAXVALUE ||
            form.operatorCode.value > this.MINVALUE ){
            alert("Please, check input fields");
            return false;
        }
        openbox(this.popUp);


        var formData = new FormData(form);


        var jsonPhone = {};

        for (const [key, value]  of formData.entries()) {
            jsonPhone[key] = value;
        }

        var table = document.getElementById("tabPhone");

        var i, row,cell1, cell2, cell3,cell4,cell5,cell6,cell7;

        if (this.mode == 0) {
            i = table.rows.length;
            row = table.insertRow(i);
            cell1 = row.insertCell(0);
            cell2 = row.insertCell(1);
            cell3 = row.insertCell(2);
            cell4 = row.insertCell(3);
            cell5 = row.insertCell(4);
            cell5.style.visibility='hidden';
            cell6 = row.insertCell(5);
            cell6.style.visibility='hidden';
            cell7 = row.insertCell(6);
            cell7.style.visibility='hidden';
            jsonPhone['timeId']=k;
            cell1.innerHTML = "<input type='checkbox'  name='phones' id="+k+">";
            k++;
            dataAdd.push(jsonPhone);
        }  else {
            i = this.pos;
            row = table.rows[i];
            cell1 = row.cells[0];
            cell2 = row.cells[1];
            cell3 = row.cells[2];
            cell4 = row.cells[3];
            cell5 = row.cells[4];
            cell6 = row.cells[5];
            cell7 = row.cells[6];
            var index =document.getElementsByName('phones')[i].getAttribute('value');
            if(!index){
                var id = document.getElementsByName('phones')[i].getAttribute('id');
                for (var j=0; j<dataAdd.length; j++){
                    var obj = dataAdd[j];
                    if(obj.timeId==id){
                        var ind = dataAdd[j].timeId;
                        jsonPhone['timeId'] = ind;
                        dataAdd[j]=jsonPhone;
                        break;
                    }
                }
                cell1.innerHTML = "<input type='checkbox'  name='phones' id="+id+">";
            }
            else{
                jsonPhone['phoneId']=index;
                dataEdit.push(jsonPhone);
                cell1.innerHTML = "<input type='checkbox'  name='phones' value="+index+">";
            }

        }



        var fullPhone = form.countryCode.value +" " +form.operatorCode.value +" "+ form.phone.value;

        cell2.innerHTML ="<input type='text' form='form' value='"+ fullPhone +"' readonly/>";

        cell3.innerHTML ="<input type='text' form='form' name='kind"+i+"' value='"+form.kind.value+"'readonly/>";
        cell4.innerHTML ="<input type='text' form='form' name='comment"+i+"' value='"+form.comment.value+"' readonly/>";
        cell5.innerHTML ="<input hidden='true' form='form' name='countryCode"+i+"' value='"+form.countryCode.value+"' />";
        cell6.innerHTML ="<input hidden='true' form='form' name='operatorCode"+i+"' value='"+form.operatorCode.value+"' />";
        cell7.innerHTML ="<input hidden='true' form='form' name='phone"+i+"' value='"+form.phone.value+"' />";
        form.reset();



    },
    editPhone: function(){
        "use strict";
        var form= document.getElementById("telephone");
        var table = document.getElementById("tabPhone");
        var checkboxes = document.getElementsByName('phones'), length = checkboxes.length;

        for (var i=0; i<length; i++) {
            if (checkboxes[i].checked){
                var row = table.rows[i];
                var kindContact =   row.cells[2].firstElementChild.getAttribute('value');
                var kind = document.getElementById('kind');
                for (var j = 0; j < kind.options.length; j++) {
                    if (kind.options[j].value.toUpperCase() == kindContact)
                        kind.options[j].selected = true;
                }
                form.comment.value =   row.cells[3].firstElementChild.getAttribute('value')
                form.countryCode.value = row.cells[4].childNodes[0].value;
                form.operatorCode.value = row.cells[5].childNodes[0].value;
                form.phone.value = row.cells[6].childNodes[0].value;



                var a = row.cells[3].childNodes[0]

                this.pos = i;
                this.mode = 1;
                openbox(this.popUp);
                break;
            }
        }
    }

};
var addAttachForm = new FormData();
var attachService = {
    timeId: Math.floor(Math.random()*10000),
    pos: 0,
    mode: 0,
    popUp: 'attachPopUp',
    saveAttach: function () {
        "use strict";
        var form= document.getElementById("attachAdd");

        var input_file = document.getElementById("attach");



        var table = document.getElementById("tabAttach");
        var i, row, cell1, cell2, cell3, cell4, cell5, cell6, cell7;
        if (attachService.mode === 0) {
            var file = form.attach.files[0];

            if(file != undefined && file.size > 1024* 1024 * 10) {
                alert("Too much size of file!Maximum size of file is 10 MB");
                return false;
            }

            if(form.attach.value == "") {
                alert("Choose file, please");
                return false;
            }
            openbox(attachService.popUp);
            i = table.rows.length;
            row = table.insertRow(i);
            cell1 = row.insertCell(0);
            cell2 = row.insertCell(1);
            cell3 = row.insertCell(2);
            cell4 = row.insertCell(3);
            cell5 = row.insertCell(4);
            cell5.style.visibility="hidden";
            cell1.innerHTML = "<input id=" + attachService.timeId + " type='checkbox' name='attaches' data-change='false'>";
            var url = window.URL.createObjectURL(file);
            cell2.innerHTML = "<a href='"+url+"'  download='"+form.file_name.value+"' >" + form.file_name.value + "</a>";
            var today = new Date();

            cell3.innerHTML = "<input value='" + today.toISOString().substring(0, 10) + "'readonly/>";

            cell5.innerHTML="<input hidden='true' name='fileName' value='"+form.file_name.value+"' readonly/>";
            addAttachForm.append("date" + attachService.timeId, today.toISOString().substring(0, 10));
            addAttachForm.append("comment" + attachService.timeId, form.attachComment.value);
            var s = "file" + attachService.timeId;
            addAttachForm.append("file" + attachService.timeId, file, form.file_name.value);
            attachService.timeId=Math.floor(Math.random()*10000);

        } else {
            openbox(attachService.popUp);
            i = attachService.pos;
            row = table.rows[i];
            cell2 = row.cells[1];
            cell4 = row.cells[3];
            cell5 = row.cells[4];
            if (!document.getElementsByName('attaches')[i].getAttribute('value')) {
                var id = document.getElementsByName('attaches')[i].getAttribute('id');
                addAttachForm.set("comment" + id, form.attachComment.value);
                var f = addAttachForm.get("file" + id);
                addAttachForm.set("file" + id, addAttachForm.get("file" + id), form.file_name.value);
                var url = window.URL.createObjectURL(f);
                cell2.innerHTML = "<a href='"+url+"'  download='"+form.file_name.value+"' >" + form.file_name.value + "</a>";
                cell5.innerHTML = " <input name='fileName' hidden='true' value='"+form.file_name.value+"'>";



            } else {
                document.getElementsByName('attaches')[i].setAttribute('data-change', 'true');
                cell2.innerHTML = "<a href='front?command=attach'  download='' >" + form.file_name.value + "</a>";
                cell5.innerHTML = " <input name='fileName' hidden='true' value='"+form.file_name.value+"'>";
            }

        }


        cell4.innerHTML = "<input type='text' name='comment' value='" + form.attachComment.value + "' readonly/>";
        // alert(document.getElementsByName('comment')[this.pos].value)
        form.reset();






    },
    deleteAttach: function () {
        var isTable = document.getElementById('tabAttach');
        var nBoxes = document.getElementsByName('attaches');
        var i, length=nBoxes.length;
        for (i=length - 1; i>=0; i--) {
            if (nBoxes[i].checked){
                if(document.getElementsByName('attaches')[i].getAttribute('data-change')=='true'){
                    document.getElementsByName('attaches')[i].setAttribute('data-change', 'false');
                    document.getElementsByName('attaches')[i].setAttribute('data-delete', 'true');
                }
                else if(!document.getElementsByName('attaches')[i].getAttribute('value')){
                    var timeId=document.getElementsByName('attaches')[i].getAttribute('id');
                    addAttachForm.delete("file"+timeId);
                    addAttachForm.delete("comment"+timeId);
                    addAttachForm.delete("date"+timeId);
                }
                else
                    attachDel.push(document.getElementsByName('attaches')[i].getAttribute('value'));
                isTable.deleteRow(i);
            }
        }
    },
    editAttach: function () {
        "use strict";
        var comment= document.getElementById("attachComment");
        var table = document.getElementById('tabAttach');

        var form = document.getElementById("attachAdd");
        var checkboxes = document.getElementsByName('attaches'), length = checkboxes.length;

        var input_file = document.getElementById("b_attach");
        var file_name = document.getElementById("b_file_name");
        input_file.style.display = "none";
        file_name.style.display = "initial";

        for (var i=0; i<length; i++) {
            if (checkboxes[i].checked) {
                var row = table.rows[i];
                form.file_name.value = row.cells[4].firstElementChild.value;
                form.attachComment.value = row.cells[3].firstElementChild.value;
                this.pos = i;
                this.mode = 1;
                openbox(this.popUp);
                break;
            }
        }
    },
    addAttach: function () {
        "use strict";
        var input_file = document.getElementById("b_attach");
        var file_name = document.getElementById("b_file_name");
        input_file.style.display = "initial";
        //file_name.style.display = "none";
        this.mode = 0;
        openbox(this.popUp);
    },
    cancelAttach: function () {
        "use strict";
        openbox(this.popUp);
        var form=document.getElementById("attachAdd");
        form.reset();



    }
}
var attachDel=[];
function changeAttach() {
    var isTable = document.getElementById('tabAttach');
    var nBoxes = document.getElementsByName('attaches');
    var json={};
    var arrDel=[];
    var arrEdit=[];
    for(var i=0; i<nBoxes.length; i++){
        if(document.getElementsByName('attaches')[i].getAttribute('data-change')=='true'){
            var jsonChange={};
            var row = isTable.rows[i];
            var a = row.cells[4].firstElementChild.getAttribute('value');


            jsonChange['attachComment']=row.cells[3].firstElementChild.getAttribute('value')
            jsonChange['fileName']= document.getElementsByName('fileName')[i].getAttribute('value');
            jsonChange['attachId']=document.getElementsByName('attaches')[i].getAttribute('value');
            arrEdit.push(jsonChange);
        }
    }
    json['del']=attachDel;
    json['change']=arrEdit;

    return json;
}

var newImagePath;
function showCover() {
    var coverDiv = document.createElement('div');
    coverDiv.id = 'cover-div';
    document.body.appendChild(coverDiv);
    document.getElementById('popup-image').setAttribute('src',
        'front?command=avatar&id='+document.getElementsByName('userImage')[0].getAttribute('value'));
}

function hideCover() {
    document.body.removeChild(document.getElementById('cover-div'));
}

function showPopup() {
    var primaryImagePath = document.getElementsByName('userImage');
    showCover();

    var container = document.getElementById('image-popup-form-container');

    document.getElementById('image-popup-message').innerHTML = "Выберите картинку:";
    document.body.style.overflow = "hidden";

    function complete() {
        document.getElementById("popupImageText").value = "";
        hideCover();
        container.style.display = 'none';
        document.body.style.overflow = ""
    }

    document.getElementById('imageOk').onclick = function () {
        var image = document.getElementsByName('userImage');
        image[0].setAttribute("src", newImagePath);
        addAttachForm.append("avatar", document.getElementById('imageInput').files[0]);
        complete();

    };

    document.getElementById('imageCancel').onclick = function() {
        var uI =  document.getElementsByName('userImage');
        uI.src=primaryImagePath[0].getAttribute('src')//.setAttribute('src', primaryImagePath);
        var pI= document.getElementById('popup-image');
        pI.setAttribute('src', primaryImagePath[0].getAttribute('src'));
        complete();
    };

    container.style.display = 'block';
}



function find() {
    var imageInput = document.getElementById("imageInput");
    imageInput.click();
};
function chn() {
    var imageInput = document.getElementById("imageInput");
    var popupText = document.getElementById("popupImageText");
    popupText.value = imageInput.value;

    newImagePath = URL.createObjectURL(event.target.files[0]);
    var popupImage = document.getElementById('popup-image');
    popupImage.src = newImagePath;

};


function validate() {
    event.preventDefault()
    var myForm = document.getElementById("contactForm");
    var name = myForm.name.value;
    var surname =  myForm.surname.value
    var city =  myForm.city.value
    if(!name){
        alert("Введите имя!!!")
        return false;
    }
    if(!surname){
        alert("Введите фамилию!!!")
        return false;
    }
    var patternForBirthday = /(\d{4})-(\d{2})-(\d{2})/;
    if(!myForm.birth.value.match(patternForBirthday)){
        alert("День рождения введен некорректно!!!");
        return false;
    }
    var pattern = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    var a = myForm.email.value.match(pattern);
    if(!a){
        alert("Email ввден некорректно!!!");
        return false;
    }

    if(!city){
        alert("Введите город!!!")
        return false;
    }
    return true;
}
function atction(id) {
    event.preventDefault();
    var myForm = document.getElementById("contactForm");
    var formData = new FormData(myForm);
    myForm.style.display = 'none';
    var header = document.getElementById('header');
    header.style.display = 'none';
    var mainForm = document.getElementById("mainForm");
    mainForm.style.display = "block";
    var footer = document.getElementById('paging');
    footer.style.display = 'block';

    var jsonContact = {};


    for (const [key, value]  of formData.entries()) {
        jsonContact[key] = value;

    }


    var test = {del: arrDel, add: dataAdd, edit: dataEdit};
    var jsonObject = {'contact': jsonContact, 'actionsPhones': test};


    var attachJson = changeAttach();
    addAttachForm.append("actions", JSON.stringify(attachJson));

    if (id === undefined)
        id = 0;
    fetch('front?command=save&contactId=' + id, {
        method: 'POST',
        body: JSON.stringify(jsonObject)
    }).then(function (response) {
        return response.json();
    }).then(function (json) {
        arrDel = [];
        dataAdd = [];
        dataEdit = [];

        fetch("front?command=setattach&contactId=" + json['contactId'], {
            method: 'POST',
            body: addAttachForm
        }).then(function (response) {
            attachDel = [];
            addAttachForm = new FormData();
            var paging = document.getElementById('paging');
            var page = paging.getAttribute('value');
            get(page);

        }).catch(function (error) {
            console.log('There has been a problem with your fetch operation: ' + error.message);
        })
    })

}
function saveContact(id) {

    var myForm = document.getElementById("contactForm");

    if (validate()==true) {

        myForm.addEventListener('submit', atction(id));
    }

}








var limit=10;

fetch('front?command=getcontacts&page=1&limit='+limit).then(function(response) {
    if(response.ok) {
        response.json().then(function(json) {
            var contact = json['contacts'];

            var data = {contacts: contact};
            var template = document.getElementById('template').innerHTML;

            //Parse it (optional, only necessary if template is to be used again)
            Mustache.parse(template);


            //Render the data into the template
            var rendered = Mustache.render(template, data);

            //Overwrite the contents of #target with the rendered HTML
            document.getElementById('target').innerHTML = rendered;
            pagingPages(json['count']);
        });
    } else {
        alert("Что-то пошло не так");
    }
});
function get(page) {
    var a = document.getElementById(page);
    var paging = document.getElementById('paging');
    paging.setAttribute('value', page);
    fetch('front?command=getcontacts&page='+page+'&limit='+limit).then(function(response) {
        if(response.ok) {


            response.json().then(function(json) {
                var contact = json['contacts'];
                var data = {contacts: contact};
                var template = document.getElementById('template').innerHTML;
                var count = json['count'];

                Mustache.parse(template);



                var rendered = Mustache.render(template, data);
                document.getElementById('target').innerHTML = rendered;
                pagingPages(count)

            });
        } else {
            alert("Что-то пошло не так");
            return 0;
        }
    });
}
function next() {
    "use strict";
    var form = document.getElementById("paging");
    var page = form.getAttribute('value');
    if(page != form.getAttribute('max'))
        get(++page);
}
function prev() {
    "use strict";
    var form = document.getElementById("paging");
    var page = form.getAttribute('value');
    if(page != 1)
        get(--page);
}


function deleteContact(){


    var nBoxes = document.getElementsByName('marked');
    var arr=[];
    var i= nBoxes.length;

    while (i--){
        if (nBoxes[i].checked == true){
            arr.push(nBoxes[i].getAttribute("value"));


        }
    }
    var jsonObj={"index": arr};


    fetch('front?command=delete', {
        method: 'POST',
        body: JSON.stringify(jsonObj)
    }).then( function(response){
        var paging = document.getElementById('paging');
        var page = paging.getAttribute('value');
        get(page)

    }).catch( function (error){

        console.log('Request failed', error);
    });

}
function pagingPages(count) {
    var pagination = document.getElementById('paging1');
    pagination.style.display = 'none';
    var pagination = document.getElementById('paging');
    pagination.innerHTML="";
    var color = "background: powderblue";
    pagination.setAttribute('style', color);
    var a = document.createElement('a');
    pagination.appendChild(a);
    a.innerHTML="<a href='javascript:{}' onclick='prev()'><<</a>";
    pagination.setAttribute('max', count);
    pagination.setAttribute('limit', limit);
    for(var i=0; i<=count; i++){
        var a = document.createElement('a');
        pagination.appendChild(a);
        a.innerHTML="<a id='"+(i+1)+"'href='javascript:{}' onclick='get("+(i+1)+")'>"+(i+1)+"</a>";
    }
    var a1 = document.createElement('a');
    pagination.appendChild(a);
    a.innerHTML="<a href='javascript:{}' onclick='next()'>>></a>"

}
function cansel() {
    var myForm = document.getElementById("contactForm");

    myForm.style.display='none';
    myForm.reset();
    var header = document.getElementById('header');
    header.style.display = 'none';
    var mainForm = document.getElementById("mainForm");
    mainForm.style.display = "block";
    var footer = document.getElementById('paging');
    footer.style.display = 'block';
    var color = "background: powderblue";
    footer.setAttribute('style', color);
}

