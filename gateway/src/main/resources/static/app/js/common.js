var _context = {
    common: false,
    constant: false,
    table: false,

}

function initCommon(layui) {
    var $ = layui.$;
    $.extend({
        formatDate: function (date, format) {
            var o = {
                "M+": date.getMonth() + 1, // month
                "d+": date.getDate(), // day
                "h+": date.getHours(), // hour
                "m+": date.getMinutes(), // minute
                "s+": date.getSeconds(), // second
                "q+": Math.floor((date.getMonth() + 3) / 3), // quarter
                "S": date.getMilliseconds() // millisecond
            }
            if (/(y+)/.test(format)) format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
            for (var k in o)
                if (new RegExp("(" + k + ")").test(format))
                    format = format.replace(RegExp.$1,
                        RegExp.$1.length == 1 ? o[k] :
                            ("00" + o[k]).substr(("" + o[k]).length));
            return format;
        },
        getUrlVariable: function (variable) {
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i = 0; i < vars.length; i++) {
                var pair = vars[i].split("=");
                if (pair[0] == variable) {
                    return pair[1];
                }
            }
            return false;
        }
    });

    $.fn.serializeObject = function (param) {
        var obj = param || {};
        $(this).each(function () {
            var a = $(this).serializeArray();
            a.forEach(function (i) {
                if (i.value) {
                    var v = obj[i.name];
                    if (v) {
                        if (v instanceof Array) {
                            v.push(i.value);
                        } else {
                            var arr = [];
                            arr.push(v, i.value);
                            obj[i.name] = arr;
                        }
                    } else {
                        obj[i.name] = i.value;
                    }
                }
            });
        });

        return obj;
    };


    // --------------------------------------
    // ????????????
    // --------------------------------------

    /*
     * ??????layer????????????????????????????????? http://www.layui.com/doc/modules/layer.html
     *
     */

    $.extend({
        infoMessage: function (message, top) {
            layer.msg(message, {icon: 6, offset: top ? top : undefined});
        },
        failMessage: function (message, top) {
            layer.msg(message, {icon: 2, offset: top ? top : undefined});
        },
        errorMessage: function (message, top) {
            layer.msg(message, {icon: 5, offset: top ? top : undefined});
        },
        successMessage: function (message, top) {
            layer.msg(message, {icon: 1, offset: top ? top : undefined});
        },
        doAlert: function (msg, icon, fun, top) {
            if (typeof fun === 'number' || typeof fun === 'string') {
                top = fun;
            }
            layer.alert(msg, {icon: icon, offset: top ? top : undefined}, function (index) {
                layer.close(index);
                if (typeof fun === 'function') fun();
            });
        },
        successAlert: function (msg, fun, top) {
            $.doAlert(msg, 1, fun, top);
        },
        warnAlert: function (msg, fun, top) {
            $.doAlert(msg, 3, fun, top);
        },
        failAlert: function (msg, fun, top) {
            $.doAlert(msg, 2, fun, top);
        },
        errorAlert: function (msg, fun, top) {
            $.doAlert(msg, 5, fun, top);
        },
        infoAlert: function (msg, fun, top) {
            $.doAlert(msg, 6, fun, top);
        },
        // ???????????????
        isLayer: function () {
            if (parent && parent.layer && parent.layer.getFrameIndex(window.name)) {
                return true;
            } else {
                return false;
            }
        },
        // ????????????frame?????????
        closeFrameLayer: function () {
            if (parent && parent.layer) {
                parent.layer.close(parent.layer.getFrameIndex(window.name));
            }
        },
        // ????????????HTML???????????????
        openPageLayer: function (content, options) {
            options = options || {};

            if (typeof options == "string") {
                options = {
                    title: options
                }
            } else if (typeof options == "function") {
                options = {
                    success: options
                };
            }

            options = $.extend(options, {
                type: 1,
                title: options.title || '',
                maxmin: true, //??????????????????????????????
                area: $.getOpenLayerSize(options.width, options.height),
                content: content,
                success: options.success
            });

            return layer.open(options);
        },
        // ????????????URL???????????????????????????????????????????????????????????????????????????URL
        openUrlLayerOrLocate: function (url, options) {
            if (options && options.data) {
                url = $.wrapGetUrl(url, options.data);
            }

            if ($.isLayer()) {
                window.location = url;
            }

            options = options || {};

            if (typeof options == "string") {
                options = {
                    title: options
                }
            } else if (typeof options == "function") {
                options = {
                    success: options
                };
            }

            options = $.extend(options, {
                type: 2,
                title: options.title || '',
                maxmin: true, //??????????????????????????????
                area: $.getOpenLayerSize(options.width, options.height),
                content: url,
                success: options.success
            })

            return layer.open(options);
        },
        // ??????????????????????????????
        getOpenLayerSize: function (w, h) {
            w = w || 0.8;
            h = h || 0.9;

            var ww = $(window).width();
            var wh = $(window).height();

            if (w > ww) {
                w = ww * 0.8;
            } else if (w <= 1) {
                w = ww * w;
            }

            if (h > wh) {
                h = wh * 0.9;
            } else if (h <= 1) {
                h = wh * h;
            }

            return [w + "px", h + "px"];
        },
        openLayerEditor: function (subOp) {
            subOp.id = subOp.id || "model_" + new Date().getTime();
            var defaultSubOp = {
                cancelBtn: false,
                editFormClass: false,
                maxColspan: 1,
                firstLabelSize: 3,
                inputSize: 8,
                labelSize: 3,
                formPaddingLeft: 10,
                formButtonBar: [{
                    id: subOp.id + '_edit_cancel_btn',
                    type: 'button',
                    name: '??????',
                    class: 'btn btn-default btn-block',
                    order: 999
                }],
                pattern: "edit"
            }

            var subOp = $.extend(defaultSubOp, subOp);


            var html = generateEditFormHtml(subOp, false);
            html = "<div style='padding-top:50px;padding-bottom:50px;padding-right:10px;padding-left:10px'>" + html + "</div>";
            var layerOption = subOp.layerOption || {};
            layerOption = $.extend({
                    success: function (layero, index) {
                        $.initComponent($(layero));
                        $("#" + subOp.id + '_edit_cancel_btn').click(function () {
                            layer.close(index);
                        });

                        if (!subOp.successCallback) {
                            subOp.successCallback = function () {
                                //???????????????????????????
                                $.successMessage("????????????");

                                layer.close(index);
                            }
                        } else {
                            var callback = subOp.successCallback;
                            subOp.successCallback = function (data) {
                                callback(data, index);
                            }
                        }

                        var subModel = new tonto.Model(subOp.id, subOp.columns, subOp);
                    }
                },
                layerOption);

            var index = $.openPageLayer(html, layerOption);
        }
    });


    // --------------------------------------
    // ajax
    // --------------------------------------

    $.extend({
        getToken: function () {
            return localStorage.getItem("token");
        },
        // ??????????????????????????????????????????
        validErrorHandler: function (response) {
            var errorHtml, error = response.data;
            if (error && $.isArray(error)) {
                var errorHtml = "<ul>?????????????????????"
                error.forEach(function (item) {
                    var el = item[1];
                    var errorMsg = item[2];
                    // ?????????????????????input???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    // form.find("#" + el + ",[name='" + el + "']").each(function() {
                    //     layer.tips(errorMsg, $(this), { time: 2000, tips: [3, 'red'] });
                    // });
                    errorHtml += "<li>" + errorMsg + "</li>";
                });
                errorHtml += "</ul>"
            } else {
                errorHtml = response.message || error || "??????????????????";
            }
            $.errorAlert(errorHtml);
        },
        // ?????????????????????
        ajaxUnLoginHandler: function () {
            // ajax?????????????????????????????????
            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????ajax????????????
            $.failAlert("????????????", function () {
                top.location.href = "/static/app/page/login.html";
            })
        },
        // ??????ajax???????????????????????????
        sendAjax: function (options) {
            // ??????ajax?????? ??????$.ajax()

            if (options.submitBtn) {
                var originComplete = options.complete;
                options.complete = function (XMLHttpRequest, textStatus) {
                    $(options.submitBtn).each(function () {
                        var that = $(this);
                        var text = that.text();
                        that.removeClass('disabled').prop('disabled', false).text(that.data("originText"));
                    });
                    if (typeof originComplete === 'function') {
                        originComplete(XMLHttpRequest, textStatus);
                    }
                };

                var originBeforeSend = options.beforeSend;
                options.beforeSend = function (XMLHttpRequest) {
                    $(options.submitBtn).each(function () {
                        var that = $(this);
                        that.data("loading", true);
                        var text = that.text();
                        that.data("originText", text);
                        that.text(text + '???...').prop('disabled', true).addClass('disabled');
                    });
                    if (typeof originBeforeSend === 'function') {
                        originBeforeSend(XMLHttpRequest);
                    }
                };
            }


            if (!options.error) {
                options.error = function (xhr, e) {
                    var code = xhr.status;
                    if (code == 200) {
                        // js????????????
                        $.errorMessage(e);
                    } else if (code == 401) {
                        $.ajaxUnLoginHandler();
                    } else if (code == 403) {
                        $.errorMessage(xhr.responseText || "????????????????????????????????????????????????");
                    } else if (code == 490) {
                        $.validErrorHandler(xhr);
                    } else {
                        var rj = xhr.responseJSON;
                        rj ? $.errorMessage(rj.message || rj.error || "????????????") :
                            $.errorMessage(xhr.responseText || "????????????");
                    }
                }
            }

            options.dataType = options.dataType || 'json';
            options.headers = options.headers || {};

            if (!options.headers.Accept) {
                options.headers.Accept = 'application/json';
            }

            options.headers["Authorization"] = $.getToken() || '';
            $.ajax(options);
        },
        // POST json???????????????Ajax??????
        postJsonAjax: function (url, data, callback, submitBtn) {
            if (typeof callback != 'function' && !submitBtn) {
                submitBtn = callback;
            }

            // ??????json??????ajax??????
            $.sendAjax({
                type: "POST",
                url: url,
                dataType: "json",
                data: data ? JSON.stringify(data) : null,
                contentType: "application/json",
                success: function (result) {
                    if (typeof callback === 'function') {
                        callback(result);
                    }
                },
                submitBtn: submitBtn
            });
        },
        // GET ??????Ajax??????
        getAjax: function (url, callback, submitBtn) {
            $.sendAjax({
                type: "GET",
                url: url,
                success: function (result) {
                    if (typeof callback === 'function') {
                        callback(result);
                    }
                },
                submitBtn: submitBtn
            });
        },
        // POST ??????Ajax??????
        postAjax: function (url, data, callback, submitBtn) {
            if (typeof data === 'function') {
                callback = data;
                data = null;
            }
            $.sendAjax({
                type: "POST",
                url: url,
                data: data,
                success: function (result) {
                    if (typeof callback === 'function') {
                        callback(result);
                    }
                },
                submitBtn: submitBtn
            });
        },
        // ???????????? Ajax??????
        postFormAjax: function (url, args, target) {
            // ??????????????????ajax
            var form = $("<form method='post' action='" + url + "' target='" + (target || "_self") + "'></form>");
            $.each(args, function (key, value) {
                if (!$.isArray(value)) {
                    value = [value];
                }

                value.forEach(function (v) {
                    var input = $("<input type='hidden'>");
                    input.attr({"name": key});
                    input.val(v);
                    form.append(input);
                });
            });
            form.appendTo(document.body);
            form.submit();
            document.body.removeChild(form[0]);
        },
        // ???????????????URL
        wrapGetUrl: function (url, data) {
            if (data) {
                var i = url.indexOf("?");
                if (i > 0) {
                    if (i < (url.length - 1)) {
                        url += "&";
                    }
                } else {
                    url += "?";
                }

                for (var o in data) {
                    var d = data[o];
                    if (d) {
                        if (!$.isArray(d)) {
                            d = [d];
                        }
                        d.forEach(function (x) {
                            url += o + "=" + x + "&";
                        });
                    }
                }
            }
            return url;
        },
        ajaxUploadFile: function (files, successCallback, submitBtn) {
            if (files) {
                if (!$.isArray(files)) {
                    files = [files];
                }

                if (files.length > 0) {
                    var formData = new FormData();
                    files.forEach(function (file) {
                        formData.append('files', file);
                    });

                    $.sendAjax({
                        url: "/common/upload/files",
                        type: "POST",
                        data: formData,
                        processData: false, // ??????jQuery??????????????????????????????
                        contentType: false, // ??????jQuery???????????????Content-Type?????????
                        success: successCallback,
                        submitBtn: submitBtn
                    });
                }
            }
        }
    });

    // -----------------------------------------
    //
    // cache
    //
    // -----------------------------------------

    window.mycache = {};

    $.extend({
        putCache: function (key, value) {
            window.mycache[key] = value;
        },
        getCache: function (key) {
            return window.mycache[key];
        }
    });

    _context.common = true;
};

function initConstant(layui) {
    if (!_context.common) {
        initCommon(layui);
    }

    var $ = layui.$;

    if (_context.dictionary) {
        _context.constant = true;
    } else {
        var json = localStorage.getItem("dictionary");
        var dictionary = json ? JSON.parse(json) : {};
        dictionary.version = dictionary.version || -1;
        $.sendAjax({
            type: "POST",
            url: "/monitor/sys/constant/get/all?version=" + dictionary.version,
            async: false,
            success: function (res) {
                if (res.json && dictionary.version != res.version) {
                    dictionary.data = JSON.parse(res.json);
                    dictionary.version = res.version;
                    localStorage.setItem("dictionary", JSON.stringify(dictionary));
                }
                _context.dictionary = dictionary;
            }
        });
    }

    $.extend({
        getConstantName: function (type, code) {
            var codeMap = _context.dictionary.data[type];
            return codeMap ? codeMap[code] : null;
        },
        getConstants: function (type) {
            return _context.dictionary.data[type];
        },
        initConstantSelect: function (select, constantType) {
            select = $(select);
            var cs = $.getConstants(constantType);
            for (var o in cs) {
                select.append("<option value='" + o + "'>" + cs[o] + "</option>");
            }
        }
    });
}

function initTable(layui) {
    if (!_context.common) {
        initCommon(layui);
    }

    var table = layui.table,
        form = layui.form,
        $ = layui.$;

    $.extend({
        renderTable: function (options) {
            var default_options = {
                method: "post",
                contentType: "application/json",
                headers: {
                    "Accept": "application/json",
                    "Authorization": $.getToken()
                },
                request: {
                    pageName: "page",
                    limitName: "limit"
                },
                parseData: function (res) {
                    res = res || [];

                    var r = {
                        "code": 0,
                        "msg": "",
                    }

                    if ($.isArray(res)) {
                        r.count = res.length;
                        r.data = res;
                    } else {
                        r.count = res.total;
                        r.data = res.data;
                    }
                    return r;
                },
                limits: [10, 15, 20, 25, 50, 100],
                limit: 15,
                page: true,
                skin: 'row',
                error: function (xhr, e) {
                    var code = xhr.status;
                    if (code == 200) {
                        $.errorMessage(e);
                    } else if (code == 401) {
                        $.ajaxUnLoginHandler();
                    } else if (code == 403) {
                        $.errorMessage(xhr.responseText || "???????????????????????????");
                    } else {
                        var rj = xhr.responseJSON;
                        rj ? $.errorMessage(rj.message || rj.error || "??????????????????") :
                            $.errorMessage(xhr.responseText || "??????????????????");
                    }
                }
            }
            options = $.extend(default_options, options);

            if (options.cols) {
                for (var m = 0; m < options.cols.length; m++) {
                    var o = options.cols[m];
                    if ($.isArray(o)) {
                        for (var n = 0; n < o.length; n++) {
                            var k = o[n];
                            if (!k.align) k.align = 'center';
                        }
                    } else {
                        o.align = 'center';
                    }
                }
            }

            if (options.searchFilter) {
                form.on('submit(' + options.searchFilter + ')', function (data) {
                    var tableId = options.id || $(options.elem).attr("id");
                    table.reload(tableId, {
                        page: {
                            curr: 1
                        },
                        where: data.field
                    }, true);
                    return false;
                });
            }

            table.render(options);
            return options;
        }
    });

    _context.table = true;
}

var styx = {}

styx.LazyCallback = function () {
    this.count = 0;
    this.data = {};
}

styx.LazyCallback.prototype.getLazyCallBack = function (callback, count) {
    var that = this;
    return function (data, field) {
        that.data[field] = data;
        that.count++;
        if (that.count >= count) {
            callback.apply(that);
        }
    }
}




