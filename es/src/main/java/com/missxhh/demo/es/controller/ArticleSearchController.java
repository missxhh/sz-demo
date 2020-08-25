package com.missxhh.demo.es.controller;

import com.missxhh.demo.es.model.Article;
import com.missxhh.demo.es.service.ArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/***
 * 文章搜索控制器
 * @Author hjf
 * @Date 2019/12/8
 **/
@Controller
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;

    // 文章页
    @RequestMapping("/article_page")
    public String articlePage(String title, @PageableDefault(value = 10, page = 0) Pageable pageable, HttpServletRequest req) {
        long startTime = System.currentTimeMillis();
        Page<Article> page = articleSearchService.queryESPage(title, pageable);
        long endTime = System.currentTimeMillis();
        req.setAttribute("time", endTime - startTime);   // 总耗时
        req.setAttribute("title", title == null ? "" : title);    // 筛选的字符串
        req.setAttribute("curPage", pageable.getPageNumber());  // 当前页

        if (page != null) {
            req.setAttribute("page", page);   // 实体信息
            req.setAttribute("total", page.getTotalElements());    // 总条数
            int totalPage = (int) ((page.getTotalElements() - 1) / pageable.getPageSize() + 1);
            req.setAttribute("totalPage", totalPage);    // 总页数
        } else {
            req.setAttribute("page", null);   // 实体信息
            req.setAttribute("total", "0");    // 总条数
            req.setAttribute("totalPage", "0");    // 总页数
        }
        return "article_page";
    }

    // 文章编辑
    @PostMapping("/article_edit")
    public String articleEdit(String edit_id, String edit_description, String edit_link, String edit_keyword, HttpServletRequest request){
        if(StringUtils.isEmpty(edit_id) || StringUtils.isEmpty(edit_description)  || StringUtils.isEmpty(edit_link)  || StringUtils.isEmpty(edit_keyword) ) {
            request.setAttribute("message", "更新参数不完整");
            return  "edit_error";
        }
        Article article = new Article();
        article.setId(edit_id);
        article.setDescription(edit_description);
        article.setLinkUrl(edit_link);
        article.setKeywords(edit_keyword);
        String res = articleSearchService.updateESData(article);
        if(StringUtils.isEmpty(res)) {
            request.setAttribute("message", res);
            return "edit_success";
        } else {
            return  "edit_error";
        }
    }

    // 文章删除
    @GetMapping("/article_delete")
    public String articleDelete(String id, HttpServletRequest request){
        if(StringUtils.isEmpty(id)) {
            request.setAttribute("message", "要删除的文档未指定");
            return  "edit_error";
        }

        String res = articleSearchService.deleteESData(id);

        if(StringUtils.isEmpty(res)) {
            request.setAttribute("message", res);
            return "edit_success";
        } else {
            return  "edit_error";
        }
    }
}
