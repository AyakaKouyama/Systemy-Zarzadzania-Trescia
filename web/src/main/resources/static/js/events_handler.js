$(document).on('click', '#send-data', function (e) {
    e.preventDefault();
    var url = $(this).closest('#send-data').attr('href');
    var canvas = document.getElementById("canvas");
    var method = document.getElementsByClassName("fa-toggle-on")[0].id;

    $('#send-data').addClass('display-none');
    $('#loading-result').removeClass('display-none');

    $.ajax({
        type: 'POST',
        url: url + "?type=" + method,
        data: canvas.toDataURL(),
        contentType: 'text/plain;charset=utf-8',
        success: function (data) {
            $('#send-data').removeClass('display-none');
            $('#loading-result').addClass('display-none');

            var split = data.split(";");
            bootbox.confirm({
                centerVertical: true,
                message: "Is it: " + split[0] + "?",
                buttons: {
                    confirm: {
                        label: 'Yes',
                        className: 'btn-success'
                    },
                    cancel: {
                        label: 'No',
                        className: 'btn-danger'
                    }
                },
                callback: function (result) {
                    if (result === true) {
                        $.ajax({
                            type: 'POST',
                            url: "/recognized" + "?imageId=" + split[1]
                        });
                    }
                }
            });
        },
        error: function (data) {
            $('#send-data').removeClass('display-none');
            $('#loading-result').addClass('display-none');
            bootbox.alert(JSON.parse(data.responseText).message);
        }

    });
});


$(document).on('click', '.icon-method', function () {
    var toggle = $(this);
    var toggles = document.getElementsByClassName("icon-method");

    for (var i = 0; i < toggles.length; i++) {
        if (toggles[i].classList.contains("fa-toggle-on")) {
            toggles[i].classList.remove("fa-toggle-on");
            toggles[i].classList.add("fa-toggle-off");
        }
    }

    if (toggles[0].classList.contains("fa-toggle-on")) {
        toggle[0].classList.remove("fa-toggle-on");
        toggles[0].classList.add("fa-toggle-off");

    } else {
        toggle[0].classList.remove("fa-toggle-off");
        toggle[0].classList.add("fa-toggle-on");
    }
});


$(document).on('click', '.process-login', function (e) {
    e.preventDefault();
    var login = $('.login').val();
    var password = $('.password').val();
    var url = $(this).attr('href');

    var dto = {};
    dto.login = login;
    dto.password = password;

    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(dto),
        contentType: 'application/json',
        dataType: 'json',
        success: function (data) {
            localStorage.setItem('token', data.accessToken);

            $.ajax({
                type: 'GET',
                url: '/admin',
                headers: {
                    'Authorization': localStorage.getItem('token')
                }
            });

        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });
});


$(document).on('click', '.test-accuracy', function (e) {
    e.preventDefault();
    var href = $(this).attr('href');

    $(this).addClass("hidden");
    $(".hidden-test-mlp").removeClass("hidden");

    $.ajax({
        type: 'GET',
        url: href,
        success: function (data) {
            $('.test-accuracy').removeClass("hidden");
            $('.hidden-test-mlp').addClass("hidden");

            if (data !== "error") {
                var keys = Object.keys(data.classAccuracy);
                var values = Object.values(data.classAccuracy);
                var str = "<b>Total: " + data.total.toFixed(2) + "%</b><br><br>";

                for (var i = 0; i < keys.length; i++) {
                    str += keys[i];
                    str += ": ";
                    str += values[i].toFixed(2);
                    str += "%<br>";
                }

                document.getElementById("accuracy-result").innerHTML = str;

            } else {
                document.getElementById("accuracy-result").innerHTML = "error";
            }
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }

    });
});


$(document).on('click', '.delete-file', function (e) {
    e.preventDefault();
    var url = $(this).attr('href');

    $.ajax({
        type: 'GET',
        url: url,
        success: function () {
            window.location.reload();
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });
});





$(document).on('click', '.select-all', function () {
    var selected = $(this)[0].checked;
    var files = document.getElementsByClassName('files');

    if (files != null) {
        for (var i = 0; i < files.length; i++) {
            files[i].checked = selected;
        }
    }
});


$(document).on('click', '.load-files', function (e) {
    e.preventDefault();
    var files = document.getElementsByClassName("files");

    $(this).addClass('hidden');
    $('.load-hidden').removeClass('hidden');

    var selectedFiles = [];
    if (files != null) {
        for (var i = 0; i < files.length; i++) {
            if (files[i].checked === true) {
                selectedFiles.push(files[i].id);
            }
        }
    }

    console.log(selectedFiles);
    var url = $(this).attr('href');
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(selectedFiles),
        contentType: 'application/json',
        dataType: 'json',
        success: function (data) {
            $('.load-files').removeClass('hidden');
            $('.load-hidden').addClass('hidden');
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });

});


$(document).on('click', '.load-file', function (e) {
    e.preventDefault();
    var icon = jQuery(this).find(".load");
    icon.removeClass('fa-upload');
    icon.addClass('fa-spinner');
    icon.addClass('fa-pulse');

    var url = $(this).attr('href');

    $.ajax({
        type: 'GET',
        url: url,
        success: function () {
            bootbox.alert({
                message: "File loaded successfully.",
                callback: function () {
                    window.location.reload();
                }
            });
        },
        error: function (data) {
            icon.addClass('fa-upload');
            icon.removeClass('fa-spinner');
            icon.removeClass('fa-pulse');
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });

});


$(document).on('click', '#customMapping', function () {
    var checked = $(this)[0].checked;
    var fileInput = $('#mapping');
    if (checked) {
        fileInput.removeClass('hidden');
    } else {
        fileInput.addClass('hidden');
    }
});


$(document).on('click', '.select-dates', function (e) {
    e.preventDefault();
    var start = $('#date-range').data('daterangepicker').startDate;
    var end = $('#date-range').data('daterangepicker').endDate;
    start = start.format('MM/DD/YYYY');
    end = end.format('MM/DD/YYYY');
    var url = $(this).attr('href');

    $(this).addClass('hidden');
    $('.select-dates-hidden').removeClass('hidden');

    $.ajax({
        type: 'GET',
        url: url + "?from=" + start + "&to=" + end,
        success: function (data) {
            $('.select-dates').removeClass("hidden");
            $('.select-dates-hidden').addClass('hidden');

            var added = data.added;
            var classified = data.classified;

            var knnAdded = data.knnAdded;
            var knnClassified = data.knnClassified;

            var mlpAdded = data.mlpAdded;
            var mlpClassified = data.mlpClassified;

            var cnnAdded = data.cnnAdded;
            var cnnClassified = data.cnnClassified;


            if (added !== 0) {
                var acc = ((parseFloat(classified) / parseFloat(added)) * 100).toFixed(2);
                document.getElementById("acc").innerHTML = "<h6>Accuracy: <b>" + acc + "%</b></h6>";
            }
            if (knnAdded !== 0) {
                var knnAcc = ((parseFloat(knnClassified) / parseFloat(knnAdded)) * 100).toFixed(2);
                document.getElementById("knnAcc").innerHTML = "<h6>Accuracy: <b>" + knnAcc + "%</b></h6>";
            }
            if (cnnAdded !== 0) {
                var cnnAcc = ((parseFloat(cnnClassified) / parseFloat(cnnAdded)) * 100).toFixed(2);
                document.getElementById("cnnAcc").innerHTML = "<h6>Accuracy: <b>" + cnnAcc + "%</b></h6>";
            }
            if (mlpAdded !== 0) {
                var mlpAcc = ((parseFloat(mlpClassified) / parseFloat(mlpAdded)) * 100).toFixed(2);
                document.getElementById("mlpAcc").innerHTML = "<h6>Accuracy: <b>" + mlpAcc + "%</b></h6>";
            }

            document.getElementById("classified").innerHTML = "<h4>General</h4><h6>Classified: <b>" + classified + "</b></h6>";
            document.getElementById("added").innerHTML = "<h6>Added: <b>" + added + "</b></h6>";
            document.getElementById("knnAdded").innerHTML = "<h4>KNN</h4><h6>Added: <b>" + knnAdded + "</b></h6>";
            document.getElementById("knnClassified").innerHTML = "<h6>Classified: <b>" + knnClassified + "</b></h6>";
            document.getElementById("mlpAdded").innerHTML = "<h4>MLP</h4><h6>Added: <b>" + mlpAdded + "</b></h6>";
            document.getElementById("mlpClassified").innerHTML = "<h6>Classified: <b>" + mlpClassified + "</b></h6>";
            document.getElementById("cnnAdded").innerHTML = "<h4>CNN</h4><h6>Added: <b>" + cnnAdded + "</b></h6>";
            document.getElementById("cnnClassified").innerHTML = "<h6>Classified: <b>" + cnnClassified + "</b></h6>";
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }

    });
});


$(document).on('click', '.save-state', function (e) {
    e.preventDefault();
    var url = $(this).attr('href');

    $.ajax({
        type: 'GET',
        url: url,
        success: function (data) {
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });

});


$(document).on('click', '.truncate-btn', function (e) {
    e.preventDefault();
    var url = $(this).attr('href');

    $(this).addClass('hidden');
    $('.truncate-hidden').removeClass('hidden');

    $.ajax({
        type: 'GET',
        url: url,
        success: function (data) {
            $('.truncate-btn').removeClass('hidden');
            $('.truncate-hidden').addClass('hidden');
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
        }
    });
});


$(document).on('click', '.terminate', function (e) {
    e.preventDefault();
    var url = $(this).attr('href');

    bootbox.confirm({
        centerVertical: true,
        message: "Are you sure you want to terminate learning process? If you press yes, the process will stop after completing latest epoch.",
        buttons: {
            confirm: {
                label: 'Yes',
                className: 'btn-success'
            },
            cancel: {
                label: 'No',
                className: 'btn-danger'
            }
        },
        callback: function (result) {
            if (result === true) {
                $.ajax({
                    type: 'GET',
                    url: url,
                    success: function (data) {

                    },
                    error: function (data) {
                        bootbox.alert(JSON.parse(data.responseText).message);
                    }
                });
            }
        }
    });
});


$(document).on('click', '#add-category', function (e) {
    var formData = {};
    formData.name = document.getElementById("category-name").value;

    var url = $(this).attr('href');
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(formData),
        contentType: 'application/json',
        success: function (data) {
            window.location.reload();
        },
        error: function (data) {
            bootbox.alert(JSON.parse(data.responseText).message);
            window.location.reload();
        }
    });
});

$(document).on('change', '#categories', function (e) {
    var id = $('#categories option:selected').val();
    window.location.href =window.location.href.replace( /[\?#].*|$/, "?category=" + id );
});

