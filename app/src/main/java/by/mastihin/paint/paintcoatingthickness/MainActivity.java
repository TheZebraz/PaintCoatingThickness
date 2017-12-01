package by.mastihin.paint.paintcoatingthickness;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends Activity implements MainView {

    @BindView(R.id.state)
    TextView stateText;

    Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        presenter = new Presenter(this, this);
    }

    @OnClick(R.id.play)
    void play(){
        presenter.play();
    }

    @OnClick(R.id.stop)
    void stop(){
        presenter.stop();
    }

    @OnTextChanged(R.id.edit_text_right_frequency_value)
    void setRightValue(CharSequence s) {
        try {
            Integer value = Integer.valueOf(s.toString());
            presenter.setRightFrequency(value);
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Not a number", Toast.LENGTH_SHORT).show();
        }
    }

    @OnTextChanged(R.id.edit_text_left_frequency_value)
    void setLeftValue(CharSequence s) {
        try {
            Integer value = Integer.valueOf(s.toString());
            presenter.setLeftFrequency(value);
        }
        catch (NumberFormatException e){
            Toast.makeText(this, "Not a number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setStateText(String state) {
        stateText.setText(getString(R.string.stateText, state));
    }
}
