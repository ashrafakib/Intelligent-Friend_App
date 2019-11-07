package mariannelinhares.techhungry.views;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
public class DrawRenderer {
    //given a canvas, a model drawing stored in memory, a color metadata,
    public static void renderModel(Canvas canvas, DrawModel model, Paint paint,
                                   int startLineIndex) {
        //minimize distortion artifacts
        paint.setAntiAlias(true);
        int lineSize = model.getLineSize();
        //given that size
        for (int i = startLineIndex; i < lineSize; ++i) {
            //get the whole line from the model object, set black
            DrawModel.Line line = model.getLine(i);
            paint.setColor(Color.BLACK);
            int elemSize = line.getElemSize();
            //if its empty, skip
            if (elemSize < 1) {
                continue;
            }
            // store that first line element in elem
            DrawModel.LineElem elem = line.getElem(0);
            //get its coordinates
            float lastX = elem.x;
            float lastY = elem.y;

            //for each coordinate in the line
            for (int j = 0; j < elemSize; ++j) {
                //get the next coordinate
                elem = line.getElem(j);
                float x = elem.x;
                float y = elem.y;
                //and draw the line between those two paints
                canvas.drawLine(lastX, lastY, x, y, paint);
                //store the coordinate as last and repeat
                //until the line is drawn
                lastX = x;
                lastY = y;
            }
        }
    }
}