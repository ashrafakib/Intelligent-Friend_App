package mariannelinhares.techhungry;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mariannelinhares.techhungry.models.Classification;
import mariannelinhares.techhungry.models.Classifier;
import mariannelinhares.techhungry.models.TensorFlowClassifier;
import mariannelinhares.techhungry.views.DrawModel;
import mariannelinhares.techhungry.views.DrawView;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private static final int PIXEL_WIDTH = 28;

    private Button clearBtn, classBtn;
    private TextView resText;
    private List<Classifier> mClassifiers = new ArrayList<>();

    private DrawModel drawModel;
    private DrawView drawView;
    private PointF mTmpPiont = new PointF();

    private float mLastX;
    private float mLastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawView) findViewById(R.id.draw);
        //get model object
        drawModel = new DrawModel(PIXEL_WIDTH, PIXEL_WIDTH);
        //init the view with the model object
        drawView.setModel(drawModel);
        // give it a touch listener to activate when the user taps
        drawView.setOnTouchListener(this);

        clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);

        classBtn = (Button) findViewById(R.id.btn_class);
        classBtn.setOnClickListener(this);

        //shows the output of the classification
        resText = (TextView) findViewById(R.id.tfRes);

        //tf load up our saved model to perform inference from local storage
        loadModel();
    }

    @Override
    protected void onResume() {
        drawView.onResume();
        super.onResume();
    }
    protected void onPause() {
        drawView.onPause();
        super.onPause();
    }
    //creates a model object in memory using the saved tensorflow protobuf model file
    //which contains all the learned weights
    private void loadModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //adding classifiers to our classifier arraylist
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "You have drawn",
                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
                                    "input", "output", true));

                } catch (final Exception e) {
                    //if they aren't found, throws error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_clear) {
            drawModel.clear();
            drawView.reset();
            drawView.invalidate();
            resText.setText("");
        }

        else if (view.getId() == R.id.btn_class) {
            //if clicked get the pixel data and store it in an array
            float pixels[] = drawView.getPixelData();
            //init an empty string to fill with the classification output
            String text = "";
            //for each classifier in our array
            for (Classifier classifier : mClassifiers) {
                //perform classification on image
                final Classification res = classifier.recognize(pixels);
                //if it can't classify, output
                if (res.getLabel() == null) {
                    text += classifier.name() + " INCORRECTLY! ☹️\nPlease practice more!!";
                } else {
                    //else output its name
                    text += String.format("%s %s CORRECTLY! ✌️\nObtained Marks: %f (On a scale of 0.0 to 1.0)\n\n              Try the next digit. ☺\n", classifier.name(), res.getLabel(),
                            res.getConf());
                }
            }
            resText.setText(text);
        }
    }

    @Override
    //this method detects which direction a user is moving their finger
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            //begin drawing
            processTouchDown(event);
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event);
            return true;
            //if finger is lifted, stop drawing
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp();
            return true;
        }
        return false;
    }

    //draw line down
    private void processTouchDown(MotionEvent event) {
        //calculate the x, y coordinates where the user has touched
        mLastX = event.getX();
        mLastY = event.getY();
        //user them to calcualte the position
        drawView.calcPos(mLastX, mLastY, mTmpPiont);
        //store them in memory to draw a line
        float lastConvX = mTmpPiont.x;
        float lastConvY = mTmpPiont.y;
        //and begin the line drawing
        drawModel.startLine(lastConvX, lastConvY);
    }

    //the main drawing function
    //it actually stores all the drawing positions
    //into the drawmodel object
    //we actually render the drawing from that object
    //in the drawrenderer class
    private void processTouchMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        drawView.calcPos(x, y, mTmpPiont);
        float newConvX = mTmpPiont.x;
        float newConvY = mTmpPiont.y;
        drawModel.addLineElem(newConvX, newConvY);

        mLastX = x;
        mLastY = y;
        drawView.invalidate();
    }

    private void processTouchUp() {
        drawModel.endLine();
    }
}