package by.mastihin.paint.paintcoatingthickness;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    public void setStateText(String state) {
        stateText.setText(getString(R.string.stateText, state));
    }
}
