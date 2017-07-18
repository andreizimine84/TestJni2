package com.testjni2;

import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.content.res.AssetManager;

public class DefaultDataGenerator implements IDataGenerator {
	Context myContext;

	public InputStream getData(Context myContext) throws NullPointerException, IOException {
		AssetManager assetManager = myContext.getAssets();
		InputStream input;
		input = assetManager.open("myfile");
		return input;
	}

}
