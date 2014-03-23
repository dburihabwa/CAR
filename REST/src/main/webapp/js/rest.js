/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var dir = function(directory) {
    if (directory === undefined || directory === null) {
        throw new Error('directory argument is not undefined!');
    }
    if (typeof directory !== 'string') {
        throw new Error('directory argument is not of type string!');
    }
    $.ajax({
        url: 'api/dir/test'
    }).done(function(data) {
        //TODO add data to table
    });
};

var uploadFile = function (path) {
    if (!path) {
        throw new Error('path must be defined!');
    }
    $.ajax('api/file/' + path, {
        type: 'POST'
    }).done(alert('hello world!'));
};

var modifyFile = function () {
};

var deleteFile = function () {
};

window.onload = function() {
    
};