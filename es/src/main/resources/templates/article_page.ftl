<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>ES Article DEMO</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap-theme.min.css"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script>
        function doFind() {
            var title = $("#title").val();
            window.location.href = "/article_page?title=" + title + "&page=0";
        }
    </script>
</head>
<body style="display: block; margin: 0 auto; width: 99%; ">
<div style="width:100%;height:60px;" align="center">
    <h2>文章列表</h2>
</div>
<br/>
<div>
    <div class="form-group form-inline">
        <label for="labelForTitle">关键字：</label>
        <input type="hidden" id="page" value="${curPage}">
        <input type="text" id="title" class="form-control" value="${title}" placeholder="搜索关键字">
    </div>
    <button type="submit" class="btn btn-default" onclick="doFind()">查找</button>
</div>
<br/>
<div class="bs-example" data-example-id="striped-table">
    <table class="table table-bordered table-hover">
        <thead>
        <tr>
            <th style="text-align:center;  gn: left;" scope="row" width="50">ID</th>
            <th style="text-align:center;" width="200">标题</th>
            <th style="text-align:center;" width="400">描述</th>
            <th style="text-align:center;" width="100">链接地址</th>
            <th style="text-align:center;" width="150">关键字</th>
            <th style="text-align:center;" width="150">操作</th>
        </tr>
        </thead>
        <tbody>
        <#if page ?exists>
            <#list page.content as p>
                <tr>
                    <th style="text-ali gn: left;">
                        ${p.id}
                    </th>
                    <th style="text-align: center;">${p.title}</th>
                    <th style="text-align: center;" width="300">${p.description}</th>
                    <th style="text-align: center;">
                        <#if p.linkUrl ?exists>
                            ${p.linkUrl}
                        </#if>
                    </th>
                    <th style="text-align: center;">
                        <#if p.keywords ?exists>
                            ${p.keywords}
                        </#if>
                    </th>
                    <th style="text-align: center;">
                        <a id="modal" onclick="doEdit(${p.id},'${p.description}','${p.linkUrl}','${p.keywords}')" role="button" class="btn" data-toggle="modal">编辑</a>
                        <a id="modal" href="/article_delete?id=${p.id}" role="button" class="btn">删除</a>
                    </th>
                </tr>
            </#list>
        </#if>
        </tbody>
    </table>
    <div style="font-size: 21px;">
        <span style="font-size: 18px;">共${total}篇文章,加载总耗时:${time}毫秒 ,当前第 ${curPage  + 1 } 页</span>
        <br/>
        跳转：
        <#list 1..totalPage as i>
            <#if title??>
                <a href="/article_page?title=${title}&page=${i-1}">${i}</a>
            <#else>
                <a href="/article_page?page=${i-1}">${i}</a>
            </#if>
        </#list>
        页
    </div>
    <div class="container">
        <div class="row clearfix">
            <div class="col-md-12 column">
                <div class="modal fade" id="modal-container" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                <h4 class="modal-title" id="myModalLabel">
                                    编辑
                                </h4>
                            </div>
                            <div class="modal-body">
                                <form role="form" id="form" action="/article_edit" method="post">
                                    <input type="hidden" class="form-control" id="edit_id" name="edit_id"/>
                                    <div class="form-group">
                                        <label for="edit_description">描述</label><input type="text" class="form-control" id="edit_description" name="edit_description" />
                                    </div>
                                    <div class="form-group">
                                        <label for="edit_link">链接地址</label><input type="text" class="form-control" id="edit_link" name="edit_link"/>
                                    </div>
                                    <div class="form-group">
                                        <label for="edit_keyword">关键字</label><input type="text" class="form-control" id="edit_keyword" name="edit_keyword"/>
                                    </div>
                                </form>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                                <button type="button" class="btn btn-primary" onclick="commit()">保存</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    function doEdit(id, description, linkUrl, keyword){
        console.log(id);
        console.log(description);
        console.log(linkUrl);
        console.log(keyword);
        $("#edit_id").val(id);
        $("#edit_description").val(description);
        $("#edit_link").val(linkUrl);
        $("#edit_keyword").val(keyword);
        $('#modal-container').modal('show')
    }
    function commit(){
        $("#form").submit();
    }
</script>
</html>