package com.github.doodler.common.page;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @Description: PageVo
 * @Author: Fred Feng
 * @Date: 14/12/2024
 * @Version 1.0.0
 */
@ApiModel(description = "PageVo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageVo<T> {

    @ApiModelProperty("Data list")
    private List<T> content;

    @ApiModelProperty("Current page")
    private int page;

    @ApiModelProperty("Page size")
    private int pageSize;

    @ApiModelProperty("Total Records")
    private long totalRecords;

    @ApiModelProperty("Next Token")
    private Object nextToken;

    @ApiModelProperty("Next Page Flag")
    private boolean nextPage;

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Page: ").append(page).append(", PageSize: ").append(pageSize).append(", Size: ")
                .append(content != null ? content.size() : 0);
        str.append(", TotalRecords: ").append(totalRecords).append(", NextPage: ").append(nextPage);
        str.append(",NextToken: ").append(nextToken);
        return str.toString();
    }

    public static <T> PageVo<T> wrap(PageResponse<T> pageResponse) {
        PageContent<T> pageContent = pageResponse.getContent();
        PageVo<T> pageVo = new PageVo<>();
        pageVo.setContent(pageContent.getContent());
        pageVo.setNextToken(pageContent.getNextToken());
        pageVo.setPage(pageResponse.getPageNumber());
        pageVo.setPageSize(pageResponse.getPageSize());
        pageVo.setTotalRecords(pageResponse.getTotalRecords());
        pageVo.setNextPage(pageResponse.hasNextPage());
        return pageVo;
    }
}
