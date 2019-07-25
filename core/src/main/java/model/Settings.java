package model;

public class Settings {
    private long count = 10;
    private long pageNum = 10;
    private long pages;
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }
    public long getPages() {
        return pages;
    }
    public long getStart(){
        return (pageNum - 1)*count;
    }
    public long countPages(long totalCount) {
        pages = totalCount/count;
        if(totalCount%count != 0)
            pages++;
        return pages;
    }
}
