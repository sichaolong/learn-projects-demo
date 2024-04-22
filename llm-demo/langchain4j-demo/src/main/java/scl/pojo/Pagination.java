package scl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination<T> {
    private int currentPage;
    private int pageSize;
    private int totalSize;
    private int totalPage;
    private List<T> items;

    public Pagination(int currentPage, int pageSize, int totalSize, List<T> items) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.items = items;
    }
}