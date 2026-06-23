package com.cherry.model.base.page;


import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author cherry
 * @version 1.0.0
 */
@Data
@Tag(name = "分页模型")
@NoArgsConstructor
@SuppressWarnings("unused")
public class CherryPageRequest {

    @Setter
    @Schema(description = "页码")
    private int pageNo;

    @Schema(description = "数量")
    private int pageSize;

    @Getter
    @Schema(hidden = true)
    private boolean rowFlag = true;

    @Getter
    @Schema(hidden = true)
    private int startRow = 0;

    @Getter
    @Schema(hidden = true)
    private int endRow = 0;

    @Getter
    @Schema(hidden = true)
    private int pageNoDuplicate;

    public CherryPageRequest(int pageNo) {
        this.pageNo = pageNo;
        this.pageSize = 15;
    }

    public CherryPageRequest(int pageNo, int pageSize) {
        checkPageSize(pageSize);
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * 手动分页页码
     * @return 分页页码
     */
    public int getPageNo() {
        return pageNo == 0 && rowFlag ? 1 : pageNo;
    }

    /**
     * 自动计算分页页码起始点
     * @return 分页页码
     */
    public int calcPage() {
        rowFlag = false;
        pageNoDuplicate = pageNo;
        return pageSize * (getPageNo() - 1);
    }

    /**
     * 自动计算oracle分页页码起始点
     */
    public void calcOraclePage() {
        int startRow = pageNo + 1;
        int endRow = pageNoDuplicate * pageSize;
    }

    public void setPageSize(int pageSize) {
        checkPageSize(pageSize);
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize == 0 ? 10 : pageSize;
    }

    public void checkPageSize(int pageSize) {
        if (pageSize > 500) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "分页参数不符合规定");
        }
    }
}
