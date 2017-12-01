package by.mastihin.paint.paintcoatingthickness;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements MainView {

    Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        presenter = new Presenter(this);
    }

    @OnClick(R.id.play)
    void play(){
        presenter.play();
    }

    @OnClick(R.id.stop)
    void stop(){
        presenter.stop();
    }

}
