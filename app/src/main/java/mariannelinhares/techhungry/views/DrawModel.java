package mariannelinhares.techhungry.views;
import java.util.ArrayList;
import java.util.List;

//a collection of getter and set functions to draw
public class DrawModel {
    public static class LineElem {
        public float x;
        public float y;
        //internal repreesntation for manipulation
        private LineElem(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
    //for a single line
    public static class Line {
        private List<LineElem> elems = new ArrayList<>();
        private Line() {
        }
        //add, get, and get index of an element
        private void addElem(LineElem elem) {
            elems.add(elem);
        }

        public int getElemSize() {
            return elems.size();
        }

        public LineElem getElem(int index) {
            return elems.get(index);
        }
    }


    private Line mCurrentLine;

    private int mWidth;
    private int mHeight;
    private List<Line> mLines = new ArrayList<>();

    //a 28x28 window
    public DrawModel(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    //start drawing line and add it to memory
    public void startLine(float x, float y) {
        mCurrentLine = new Line();
        mCurrentLine.addElem(new LineElem(x, y));
        mLines.add(mCurrentLine);
    }

    public void endLine() {
        mCurrentLine = null;
    }

    public void addLineElem(float x, float y) {
        if (mCurrentLine != null) {
            mCurrentLine.addElem(new LineElem(x, y));
        }
    }

    public int getLineSize() {
        return mLines.size();
    }

    public Line getLine(int index) {
        return mLines.get(index);
    }

    public void clear() {
        mLines.clear();
    }
}