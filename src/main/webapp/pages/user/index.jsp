<%@page language="java" contentType="text/html; charset=utf-8"
        pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>JS學習</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/index.css">
    <script src="${pageContext.request.contextPath}/jquery.js"></script>
</head>
<body>
<div class="container">
    <div class="form">
        <div class="first">
            <p>Name: <input id="name" type="text"></p>
            <p>Address: <input type="text" id="address"></p>
        </div>
        <div class="second">
            <p>
                Sex: <select id="sex">
                <option value="1">男</option>
                <option value="0">女</option>
            </select>
            </p>
            <p>Email: <input type="text" id="email"></p>
        </div>
        <div class="third">
            <p>Birthday: <input type="text" class="birth layui-bg-orange" id="birth" placeholder="^_^">
            </p>
            <p>PostCode: <input type="text" id="postcode"></p>
            <p id="pca">
                城市:
                <select class="p_val">
                    <option value=""></option>
                </select>
                <select class="c_val">
                    <option value=""></option>
                </select>
                <select class="a_val">
                    <option value=""></option>
                </select>
            </p>
        </div>
        <div class="third">
            <p>Description: <textarea id="description" cols="90" rows="5"></textarea></p>
        </div>

        <div class="fourth">
            <button id="add" onclick="handleAdd()">Add</button>
            <button id="delete_all">Reset</button>
        </div>
    </div>
    <table id="tab">
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Sex</th>
            <th>Address</th>
            <th>Email</th>
            <th>Option</th>
        </tr>
        </thead>
        <tbody></tbody>
    </table>
    <div class="fourth">
        <button id="prev" onclick="prev()">Prev</button>
        <button id="next" onclick="next()">Next</button>
    </div>
    <div class="mask" onclick="hide()">
        <div class="tk" id="tk">
            <div id="data">
            </div>
            <div class="fourth">
                <button onclick="hide()">Back</button>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/laydate.js"></script> <!-- 改成你的路径 -->
<script>
    // 日期選擇
    lay('#version').html('-v' + laydate.v);
    laydate.render({
        elem: '#birth', //指定元素
        lang: 'en',
        theme: 'green'
    });

    var $_name = $("#name");
    var $address = $("#address");
    var $sex = $("#sex");
    var $email = $("#email");
    var $tab = $($("#tab>tbody")[0]);
    var $birthday = $("#birth");
    var $postcode = $("#postcode");
    var $description = $("#description");

    var $_next = $("#next");
    var $_prev = $("#prev");
    var $mask = $(".mask");
    var $tk = $($("#data")[0]);

    var $pcaSelect = $("#pca select");

    let next_page_url = "";
    let prev_page_url = "";
    let current_page = 1;

    $(document).ready(function () {

        render();

        $("#delete_all").click(function () {
            $_name.val("");
            $address.val("");
            $email.val("");
            $sex.val("");
            $birthday.val("");
            $postcode.val("");
            $description.val("");
        });
    });

    function handleAdd() {
        var p_val = $(".p_val").find("option:selected").val();
        var c_val = $(".c_val").find("option:selected").val();
        var a_val = $(".a_val").find("option:selected").val();
        var p_text = $(".p_val").find("option:selected").text();
        var c_text = $(".c_val").find("option:selected").text();
        var a_text = $(".a_val").find("option:selected").text();

        var formData = {
            name: $("#name").val(),
            sex: parseInt($("#sex").val()),
            address: $("#address").val(),
            email: $("#email").val(),
            birthday: $("#birth").val(),
            postcode: $("#postcode").val(),
            description: $("#description").val(),
            province_id: parseInt(p_val),
            province: p_text,
            city_id: parseInt(c_val),
            city: c_text,
            area_id: parseInt(a_val),
            area: a_text,
        };
        $.ajax({
            url: "http://localhost:8090/app/users/store",
            method: "post",
            contentType: "application/json;charset utf-8",
            data: JSON.stringify(formData),
            dataType: "json",
            success: function () {
                render();
            }
        });
    }

    function del(i) {
        $.ajax({
            url: "http://localhost:8090/app/users/delete/" + i,
            method: "delete",
            success: function () {
                prev_page_url = prev_page_url.replace(/page=[\d]+/, "page=" + current_page);
                render(prev_page_url);
            }
        });
    }

    function render(url) {
        var str = "";
        url = url ? url : "http://localhost:8090/app/users/index";
        $.get(url, function (data) {
            var meta = data['meta'];
            data = data['data'];
            for (var i in data) {
                str = str + "<tr><td>" + data[i].id + "</td><td>" + data[i].name + "</td><td>" + (data[i].sex == "1" ? "男" : "女") + "</td><td>" + data[i].address + "</td><td>" + data[i].email + "</td><td><button id='delete' onclick='del(" + data[i].id + ")'>Delete</button><button id='detail' onclick='detail(" + data[i].id + ")'>Detail</button></td></tr>";
            }
            $tab.html(str);
            $_next.attr('disabled', meta.next_page_url === "");
            $_prev.attr('disabled', meta.prev_page_url === "");
            next_page_url = meta.next_page_url;
            prev_page_url = meta.prev_page_url;
            current_page = meta.current_page;
        });
    }

    function prev() {
        render(prev_page_url);
    }

    function next() {
        render(next_page_url);
    }

    function show() {
        $mask.show('fast');
    }

    function hide() {
        $mask.hide('fast');
    }

    function detail(i) {
        let content = "";
        show();
        $.get("http://localhost:8090/app/users/detail/" + i, function (data) {
            content += "<p>Name：" + (data.name) + "</p><p>Sex：" + (data.sex == "1" ? "男" : "女") + "</p>";
            content += "<p>Province： " + (data.province) + "</p><p>City：" + (data.city) + "</p>";
            content += "<p>Area： " + (data.area) + "<p>Address： " + (data.address) + "</p><p>Email：" + (data.email) + "</p>";
            content += "<p>Birthday： " + (data.birthday) + "</p><p>PostCode：" + (data.postcode) + "</p>";
            content += "<p>Description： " + (data.description) + "</p>";
            content += "<p>Created_at： " + (data.created_at) + "</p>";
            content += "<p>Updated_at： " + (data.updated_at) + "</p>";
            $tk.html(content);
        });
    }

    function setPca(parent_id = 0, $select) {
        $.get(" http://59.111.58.150:8102/api/region?parent_id=" + parent_id, function (data) {
            var option = '<option></option>';
            for (var i in data) {
                option += "<option value='" + data[i].id + " ' data-label='" + data[i].name + "'>" + data[i].name + "</option>"
            }
            $select.html(option);
        });
    }

    $pcaSelect.change(function () {
        setPca($(this).val(), $(this).next())
    });
    setPca(0, $pcaSelect.first())

</script>
</body>
</html>