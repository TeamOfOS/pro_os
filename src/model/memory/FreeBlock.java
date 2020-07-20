package model.memory;

/**
 * 空闲块
 */
public class FreeBlock {
    private int startIndex;//起址
    private int endIndex;//终址
    private int length;//长度

    FreeBlock(int startIndex, int length) {
        this.startIndex = startIndex;
        this.length = length;
    }

    int getStartIndex() {
        return startIndex;
    }

    int getLength() {
        return length;
    }

    int getEndIndex(){
        return startIndex+length;
    }

    void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "FreeBlock{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", length=" + length +
                '}';
    }
}
