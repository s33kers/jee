$(document).ready(function() {

    // move thymeleaf generated checkbox hidden fields above the checkbox itself
    $.each($("input[type='checkbox']"), function(key, value){
        var hiddenField = $(this).next("input[type='hidden']");
        if(hiddenField.length === 0)
            return;

        // move the field
        $(this).before(hiddenField);
    });

    $(".datepicker").pickadate({
        selectMonths: true,
        selectYears: 10,
        format: "yyyy-mm-dd"
    });

    Materialize.updateTextFields();
    $('select').material_select();

    var editPageSelector = $("#edit-survey-select-page");
    if(editPageSelector.length > 0){
        editPageSelector.change(function() {
            var surveyId = $("#survey-id").val();
            var currentPage = $("#survey-current-page").val();
            if($(this).val() === currentPage)
                return;

            var redirectUrl = "/auth/survey-edit/" + surveyId + "?page=" + $(this).val();
            confirmClear(function () { // confirm callback
                window.location = redirectUrl;
            });
        })
    }

    var pageSelector = $("#survey-select-page");
    if(pageSelector.length > 0){
        pageSelector.change(function() {
            var surveyId = $("#survey-id").val();
            var currentPage = $("#survey-current-page").val();
            if($(this).val() === currentPage)
                return;

            window.location = "/auth/survey-edit/preview/" + surveyId + "?page=" + $(this).val();
        });
    }

    function getIdPostfix() {
        return "" + Math.random() + "_" + Date.now();
    }

    $(".submit-form-btn").click(function(e){
        e.preventDefault();

        var formid = $(this).data("formid");
        var form = $("#" + formid);

        var confirmCallback = function () {
            if(form.length === 1 && form.is("form")){
                form.submit();
            }
        };

        if (form.hasClass('confirm-everything')) {
            confirmClear(confirmCallback);
        } else if (form.hasClass('confirm-header')) {
            confirmHeaderClear(confirmCallback);
        } else {
            confirmCallback();
        }
    });

    (function() {
        var addButtonsContainer = $("#add-buttons-container");
        if(addButtonsContainer.length === 0)
            return;

        var addTextQbtn = $("#add-text-question");
        var addCheckboxQbtn = $("#add-checkbox-question");
        var addMultipleChoiceQbtn = $("#add-multiplechoice-question");
        var addScaleQbtn = $("#add-scale-question");

        var questions = $(".question-container");
        var nextQid = 1;
        if(questions.length > 0)
            nextQid = $(questions[questions.length - 1]).data("questionid") + 1;

        function row(inner){
            return $("<div>").addClass("row").append(inner);
        }

        function col(inner, isInputField){
            var elem = $("<div>").addClass("col").addClass("s12");
            if(isInputField)
                elem.addClass("input-field");
            elem.append(inner);

            return elem;
        }

        function createTypeField(type){
            return row(col(function(){
                var index = nextQid - 1;
                var elements = $();

                var input = $("<input>")
                    .attr("type", "hidden")
                    .attr("name", "questions[" + index + "].questionType")
                    .attr("value", type);
                var text = $("<span>")
                    .append("Type: ")
                    .append($("<em>").append(type));

                elements = elements.add(input);
                elements = elements.add(text);

                return elements;
            }(), false))
        }

        function fixInputNameIndexes() {
            $.each($(".question-container"), function(i, e) {
                if(parseInt($(e).data("questionid")) !== (i+1)) {
                    $(e).data("questionid", (i+1));

                    var titleElem = $(e).find(".card-title")[0];
                    var title = $(titleElem).html();
                    title = title.replace(/Question #\d/, "Question #" + (i+1));
                    $(titleElem).html(title);

                    var inputs = $(e).find("input");
                    $.each(inputs, function(j, input) {
                        var name = $(input).attr("name");
                        name = name.replace(/questions\[\d]/, "questions["+i+"]");
                        $(input).attr("name", name);
                    });

                    var addOptionBtn = $(e).find(".add-option-btn");
                    if(addOptionBtn.length !== 0) {
                        addOptionBtn.data("questionindex", i);
                    }
                }
            });
        }

        function createQuestion(specificQuestionFields) {
            var container = $("<div>")
                .addClass("container")
                .addClass("question-container")
                .data("questionid", nextQid);

            var card = $("<div>")
                .addClass("card")
                .addClass("blue")
                .appendTo(container);

            var cardTitle = $("<span>")
                .addClass("card-title")
                .append("Question #" + nextQid);

            var deleteQuestionBtn = $("<a>")
                .addClass("btn-floating")
                .addClass("halfway-fab")
                .addClass("waves-effect")
                .addClass("red")
                .attr("href", "#!")
                .attr("data-toggle", "tooltip")
                .attr("title", "Delete question")
                .append($("<i>").addClass("material-icons").append("remove"));

            $("<div>").addClass("card-content")
                .addClass("blue")
                .addClass("lighten-1")
                .addClass("white-text")
                .append(cardTitle)
                .append(deleteQuestionBtn)
                .appendTo(card);

            var inputs = $("<div>").addClass("card-content")
                .addClass("grey")
                .addClass("lighten-5");

            inputs.append(specificQuestionFields);
            inputs.appendTo(card);

            deleteQuestionBtn.click(function(e){
                e.preventDefault();

                container.remove();
                fixInputNameIndexes();
            });

            nextQid += 1;

            return container;
        }

        function createCommonFields() {
            var fields = $();

            var index = nextQid - 1;

            var questionText = row(col(function(){
                var elements = $();

                var field = $("<input>")
                    .attr("type", "text")
                    .attr("id", "question_" + getIdPostfix())
                    .attr("name", "questions[" + index + "].question")
                    .attr("placeholder", "Enter your question here")
                    .attr("maxlength", 255)
                    .addClass("form-control")
                    .addClass("dirty");

                var label = $("<label>")
                    .attr("for", field.attr("id"))
                    .append("Question");

                elements = elements.add(label);
                elements = elements.add(field);

                return elements;
            }(), true));
            fields = fields.add(questionText);

            var mandatoryCheckbox = row(col(function(){
                var elements = $();

                var field = $("<input>")
                    .attr("type", "checkbox")
                    .attr("id", "mandatory_" + getIdPostfix())
                    .attr("name", "questions[" + index + "].mandatory")
                    .addClass("form-control")
                    .addClass("dirty");

                var label = $("<label>")
                    .attr("for", field.attr("id"))
                    .append("Mandatory");

                elements = elements.add(field);
                elements = elements.add(label);

                return elements;
            }(), false));
            fields = fields.add(mandatoryCheckbox);

            return fields;
        }

        function createTextQuestion() {
            var fields = createTypeField("TEXT")
                .add(createCommonFields());

            return fields;
        }

        function fixOptionFields(addOptionBtnContainer) {
            var optionFields = addOptionBtnContainer.parent().find("input.optionField");
            $.each(optionFields, function(i, e) {
                var name = $(e).attr("name");
                name = name.replace(/chooseQuestionOptions\[\d]/g, "chooseQuestionOptions["+i+"]");
                $(e).attr("name", name);

                var placeHolder = $(e).attr("placeholder");
                placeHolder = placeHolder.replace(/#\d/g, "#" + (i+1));
                $(e).attr("placeholder", placeHolder);
            });

            var addOptionBtn = addOptionBtnContainer.find(".add-option-btn");
            addOptionBtn.data("nextoptionid", optionFields.length);
        }

        function addOption(addOption) {
            var addOptionBtnContainer = $(addOption).closest(".add-option-container");
            var i = $(addOption).data("questionindex");
            var oid = $(addOption).data("nextoptionid");

            var optionCol = col(function() {
                var elements = $();

                var field = $("<input>")
                    .attr("type", "text")
                    .attr("id", "option_" + i + "_" + getIdPostfix())
                    .attr("name", "questions[" + i + "].chooseQuestionOptions[" + oid + "].option")
                    .attr("placeholder", "Enter the question's option #" + (oid+1) + " here")
                    .attr("maxlength", 255)
                    .addClass("optionField")
                    .addClass("form-control")
                    .addClass("dirty");

                var label = $("<label>")
                    .attr("for", field.attr("id"))
                    .append("Option");

                elements = elements.add(label);
                elements = elements.add(field);

                var deleteOptionBtn = $("<a>")
                    .addClass("btn-floating")
                    .addClass("halfway-fab")
                    .addClass("waves-effect")
                    .addClass("red")
                    .addClass("remove-option-btn")
                    .attr("href", "#!")
                    .attr("data-toggle", "tooltip")
                    .attr("title", "Delete option")
                    .append($("<i>").addClass("material-icons").append("remove"));
                elements = elements.add(deleteOptionBtn);

                deleteOptionBtn.click(function(e){
                    e.preventDefault();

                    option.remove();
                    fixOptionFields(addOptionBtnContainer);
                });

                return elements;
            }(), true).addClass("option-field");
            var option = row(optionCol);

            oid += 1;
            $(this).data("nextoptionid", oid);

            addOptionBtnContainer.before(option);
            Materialize.updateTextFields();
            fixOptionFields(addOptionBtnContainer);
        }

        function createMultichoiceFields(){
            var index = nextQid - 1;

            var buttonContainer = row(col(function() {
                return $("<a>")
                    .addClass("btn")
                    .addClass("waves-effect")
                    .addClass("blue")
                    .addClass("add-option-btn")
                    .data("questionindex", index)
                    .data("nextoptionid", 0)
                    .attr("href", "#!")
                    .append("Add new option")
                    .click(function(e){
                        e.preventDefault();

                        addOption($(this));
                    });
            }(), false)).addClass("add-option-container");

            return buttonContainer;
        }

        function createCheckboxQuestion() {
            var fields = createTypeField("CHECKBOX");
            fields = fields.add(createCommonFields());

            fields = fields.add(createMultichoiceFields());

            return fields;
        }

        function createMultipleChoiceQuestion() {
            var fields = createTypeField("MULTIPLECHOICE");
            fields = fields.add(createCommonFields());

            fields = fields.add(createMultichoiceFields());

            return fields;
        }

        function createScaleQuestion() {
            var index = nextQid - 1;
            var fields = createTypeField("SCALE");
            fields = fields.add(createCommonFields());

            var minValue = row(col(function(){
                var elements = $();

                var input = $("<input>")
                    .attr("type", "number")
                    .attr("id", "minValue_" + getIdPostfix())
                    .attr("name", "questions[" + index + "].minValue")
                    .attr("placeholder", "Enter the minimum value here")
                    .attr("step", 0.01)
                    .attr("min", -999999999.99)
                    .attr("max", 999999999.99)
                    .addClass("form-control")
                    .addClass("dirty");
                var label = $("<label>")
                    .attr("for", input.attr("id"))
                    .append("Minimum value");

                elements = elements.add(input);
                elements = elements.add(label);

                return elements;
            }(), true));
            fields = fields.add(minValue);

            var minValue = row(col(function(){
                var elements = $();

                var input = $("<input>")
                    .attr("type", "number")
                    .attr("id", "maxValue_" + getIdPostfix())
                    .attr("name", "questions[" + index + "].maxValue")
                    .attr("placeholder", "Enter the maximum value here")
                    .attr("step", 0.01)
                    .attr("min", -999999999.99)
                    .attr("max", 999999999.99)
                    .addClass("form-control")
                    .addClass("dirty");
                var label = $("<label>")
                    .attr("for", input.attr("id"))
                    .append("Maximum value");

                elements = elements.add(input);
                elements = elements.add(label);

                return elements;
            }(), true));
            fields = fields.add(minValue);

            return fields;
        }

        if(addTextQbtn.length > 0){
            addTextQbtn.click(function(e) {
                e.preventDefault();

                addButtonsContainer.before(createQuestion(createTextQuestion()));
                Materialize.updateTextFields();
                fixInputNameIndexes();
            });
        }

        if(addCheckboxQbtn.length > 0){
            addCheckboxQbtn.click(function(e) {
                e.preventDefault();

                addButtonsContainer.before(createQuestion(createCheckboxQuestion()));
                Materialize.updateTextFields();
                fixInputNameIndexes();
            });
        }

        if(addMultipleChoiceQbtn.length > 0){
            addMultipleChoiceQbtn.click(function(e){
                e.preventDefault();

                addButtonsContainer.before(createQuestion(createMultipleChoiceQuestion()));
                Materialize.updateTextFields();
                fixInputNameIndexes();
            });
        }

        if(addScaleQbtn.length > 0){
            addScaleQbtn.click(function(e){
                e.preventDefault();

                addButtonsContainer.before(createQuestion(createScaleQuestion()));
                Materialize.updateTextFields();
                fixInputNameIndexes();
            });
        }

        $(".delete-question").click(function(e) {
            e.preventDefault();
            $(this).closest(".question-container").remove();
            fixInputNameIndexes();
        });

        $(".remove-option-btn").click(function(e) {
            e.preventDefault();
            var addOptionBtnContainer = $(this).closest(".option-container").parent().find(".add-option-container");
            $(this).closest(".option-container").remove();
            fixOptionFields(addOptionBtnContainer);
        });

        $(".add-option-btn").click(function(e) {
            e.preventDefault();
            addOption(this);
        });
    })();

    (function() {

        $(".url-copy-btn").click(function(e){
            var $temp = $("<input>");
            $("body").append($temp);
            var url = $("#surveyUrl").val();
            $temp.val(url).select();
            document.execCommand("copy");
            $temp.remove();
        });

        function gotoPage(nextPage) {
            var maxPages = $("#survey-max-page").val();

            if(nextPage > 1) {
                $("#backBtn").removeClass("disabled");
            }

            if(nextPage === 1) {
                if(!$("#backBtn").hasClass("disabled"))
                    $("#backBtn").addClass("disabled");
            }

            if(nextPage == maxPages) {
                $("#submit-form").css('display', 'block');

                if(!$("#nextBtn").hasClass("disabled"))
                    $("#nextBtn").addClass("disabled");
            } else {
                $("#submit-form").css('display', 'none');
            }

            if(nextPage < maxPages) {
                $("#nextBtn").removeClass("disabled");
            }

            $(".page").css('display', 'none');
            $("#page-" + nextPage).css('display', 'block');

            $("#survey-current-page").val(nextPage);
            $("#span-current-page").text(nextPage);

            Materialize.updateTextFields();
        }

        $("#nextBtn").click(function(e){
            e.preventDefault();
            var maxPages = $("#survey-max-page").val();
            var currentPage = $("#survey-current-page").val();
            var nextPage = parseInt(currentPage)+1;

            if(nextPage <= maxPages) {
                $("#backBtn").removeClass("disabled");

                if(nextPage == maxPages) {
                    $("#submit-form").css('display', 'block');

                    if(!$("#nextBtn").hasClass("disabled"))
                        $("#nextBtn").addClass("disabled");
                }

                $(".page").css('display', 'none');
                $("#page-" + nextPage).css('display', 'block');

                $("#survey-current-page").val(nextPage);
                $("#span-current-page").text(nextPage);

                Materialize.updateTextFields();
            }
        });

        $("#backBtn").click(function(e){
            e.preventDefault();
            var currentPage = $("#survey-current-page").val();
            var prevPage = parseInt(currentPage)-1;

            if(prevPage > 0){
                $("#submit-form").css('display', 'none');

                $(".page").css('display', 'none');
                $("#page-" + prevPage).css('display', 'block');
                $("#survey-current-page").val(prevPage);
                $("#span-current-page").text(prevPage);

                $("#nextBtn").removeClass("disabled");
                if(prevPage === 1 && !$("#backBtn").hasClass("disabled"))
                    $("#backBtn").addClass("disabled");

                Materialize.updateTextFields();
            }
        });

        $.each($("input[type='number'][data-pageid]"), function(i, e) {
            e.oninvalid = function() {
                var page = $(e).data("pageid");
                if(page == $("#survey-current-page").val())
                    return;

                gotoPage(page);
            }
        });

        $("#up-btn").click(function(e){
            $(window).scrollTop(0);
        });

        $("#save-answer").click(function(e){
            if ( $(this).hasClass( "indigo" ) ) {
                e.preventDefault();
            }

            $("#submit-email-button").addClass('s6').removeClass('s12');
            $("#save-answer").addClass('red').removeClass('indigo');
            $("#email-field").css('display', 'block');
            $("#email").addClass("validate");
        });

        $("#email").blur(function(){
            var email = $("#email").val();
            if(email.length == 0) {
                $("#email-field").css('display', 'none');
                $("#submit-email-button").addClass('s12').removeClass('s6');
                $("#save-answer").addClass('indigo').removeClass('red');
                $("#email").removeClass("validate");
            }
        });
    })();

    $("input.form-control, textarea.form-control").on('change', function () {
        $(this).addClass('dirty');
    });

    function confirmClear(confirmCallback) {
        var editedCount = $(".form-control.dirty").length;
        if (editedCount === 0) {
            confirmCallback();
        }else {
            showYesNoDialog("You edited at least 1 field, but did not save changes! Do you want to discard changes?", confirmCallback, function() {});
        }
    }

    function confirmHeaderClear(confirmCallback) {
        var editedCount = $("#survey-settings-fragment .form-control.dirty").length;
        if (editedCount === 0) {
            confirmCallback();
        } else {
            showYesNoDialog("You edited at least 1 field, but did not save changes! Do you want to discard changes?", confirmCallback, function() {});
        }
    }

    function showDialog(title, message, cancelButtonText, confirmButtonText, dismissible, cancelCallback, confirmCallback, closeCallback) {
        var modal = $("#dialog");
        $(".modal-title", modal).text(title);
        $(".modal-message", modal).text(message);
        if (cancelButtonText) {
            if (cancelCallback) {
                $(".modal-cancel", modal).text(cancelButtonText).on("click", cancelCallback).show();
            } else {
                $(".modal-cancel", modal).text(cancelButtonText).show();
            }
        } else {
            $(".modal-cancel", modal).hide();
        }
        if (confirmButtonText) {
            if (confirmCallback) {
                $(".modal-confirm", modal).text(confirmButtonText).on("click", confirmCallback).show();
            } else {
                $(".modal-confirm", modal).text(confirmButtonText).show();
            }
        } else {
            $(".modal-confirm", modal).hide();
        }
        modal.modal({
            dismissible: dismissible,
            complete: closeCallback
        }).modal("open");
    }

    function showAlertDialog(message, closeCallback) {
        showDialog("Hey, listen!", message, false, "OK", true, null, null, closeCallback);
    }

    function showConfirmDialog(message, confirmCallback, cancelCallback) {
        showDialog("What's up? I need your confirmation!", message, "Cancel", "Confirm", false, cancelCallback, confirmCallback, null);
    }

    function showYesNoDialog(message, yesCallback, noCallback) {
        showDialog("Hey!", message, "No", "Yes", false, noCallback, yesCallback, null);
    }

    function generateNewRandomColor(currentColors) {
        var r = Math.floor(Math.random() * 255);
        var g = Math.floor(Math.random() * 255);
        var b = Math.floor(Math.random() * 255);
        var a = 0.5;

        var color = "rgba("+r+","+g+","+b+","+a+")";
        if($.inArray(color, currentColors) !== -1)
            color = generateNewRandomColor(currentColors);
        return color;
    }

    $.each($(".pie-chart-data"), function(i, e) {
        var element = $(e);
        var chart = $("<canvas>");
        var chartContainer = $("<div>").addClass("pie-chart-container").append(chart);
        element.after(chartContainer);
        var ctx = chart[0].getContext("2d");

        var data = {
            datasets: [{
                data: [],
                backgroundColor: []
            }],

            labels: []
        };

        $.each(element.find(".chart-data"), function(j, el) {
            data.datasets[0].backgroundColor.push(generateNewRandomColor(data.datasets.backgroundColor));
            data.datasets[0].data.push($(el).data("value"));
            data.labels.push($(el).data("title"));
        });

        var pieChart = new Chart(ctx, {
            type: "pie",
            data: data,
            options: {
                tooltips: {
                    mode: "label",
                    callbacks: {
                        label: function(tooltipItem, data) {
                            var index = tooltipItem['index'];
                            return data.labels[index] + ": " + data.datasets[0].data[index] + "%";
                        }
                    }
                }
            }
        });
    });

    $.each($(".bar-chart-data"), function(i, e) {
        var element = $(e);
        var chart = $("<canvas>");
        var chartContainer = $("<div>").append(chart);
        element.after(chartContainer);
        var ctx = chart[0].getContext("2d");

        var data = {
            datasets: [{
                data: [],
                backgroundColor: []
            }],

            labels: []
        };

        $.each(element.find(".chart-data"), function(j, el) {
            data.datasets[0].backgroundColor.push(generateNewRandomColor(data.datasets.backgroundColor));
            data.datasets[0].data.push($(el).data("value"));
            data.labels.push($(el).data("title"));
        });

        var barChart = new Chart(ctx, {
            type: "bar",
            data: data,
            options: {
                tooltips: {
                    mode: "label",
                    callbacks: {
                        label: function(tooltipItem, data) {
                            var index = tooltipItem['index'];
                            return data.datasets[0].data[index] + "%";
                        }
                    }
                },
                legend: {
                    display: false
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            // Include a dollar sign in the ticks
                            callback: function(value, index, values) {
                                return value + "%";
                            },
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    });

});