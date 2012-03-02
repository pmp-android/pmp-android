/*
 * Copyright 2012 pmp-android development team
 * Project: App3
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.apps.infoapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;

public class InfoAppActivity extends FragmentActivity {

	private ViewPager mPager;
	private InfoAppFragmentPagerAdapter mAdapter;
	private CirclePageIndicator mIndicator;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mPager = (ViewPager) findViewById(R.id.viewpager);
		mAdapter = new InfoAppFragmentPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.navigation);
		mIndicator.setViewPager(mPager);
		mIndicator.setCurrentItem(0);

		// You can also do: indicator.setViewPager(pager, initialPage);

	}
}