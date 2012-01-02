package de.unistuttgart.ipvs.pmp.apps.vhike.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.unistuttgart.ipvs.pmp.R;
import de.unistuttgart.ipvs.pmp.apps.vhike.ctrl.Controller;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Users can register an account when register form is filled in correctly
 * 
 * @author andres
 * 
 */
public class RegisterActivity extends Activity {

	EditText et_username;
	EditText et_email;
	EditText et_password;
	EditText et_pw_confirm;
	EditText et_firstname;
	EditText et_lastname;
	EditText et_mobile;
	EditText et_desc;

	boolean cEmail = false;
	boolean cPw = false;
	boolean cMobile = false;
	boolean registrationSuccessfull = false;

	private final Pattern email_pattern = Pattern
			.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
	private final Pattern mobile_pattern = Pattern
			.compile("^[+]?[0-9]{10,13}$");

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		et_username = (EditText) findViewById(R.id.et_username);
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_pw);
		et_pw_confirm = (EditText) findViewById(R.id.et_pw_confirm);
		et_firstname = (EditText) findViewById(R.id.et_firstname);
		et_lastname = (EditText) findViewById(R.id.et_lastname);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_desc = (EditText) findViewById(R.id.et_description);

		validator();
		register();

	}

	private void validator() {
		et_pw_confirm.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				if (et_pw_confirm.getText().toString()
						.equals(et_password.getText().toString())) {
					cPw = true;
				} else {
					cPw = false;
					et_pw_confirm.setError("Passwords do not match");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

		});

		et_email.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				if (!email_pattern.matcher(et_email.getText().toString())
						.matches()) {
					et_email.setError("Invalid email");
					cEmail = false;
				} else {
					cEmail = true;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

		});

		et_mobile.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!mobile_pattern.matcher(et_mobile.getText().toString())
						.matches()) {
					et_mobile.setError("Invalid phone number");
					cMobile = false;
				} else {
					cMobile = true;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});

		// et_email.addTextChangedListener(new InputValidator(et_email, "",
		// cEmail, cMobile));
		// et_mobile.addTextChangedListener(new InputValidator(et_mobile, "",
		// cEmail, cMobile));
	}

	private void register() {
		Button register = (Button) findViewById(R.id.Button_Register);
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Controller ctrl = new Controller();
				Map<String, String> map = new HashMap<String, String>();

				EditText et_username = (EditText) findViewById(R.id.et_username);

				et_email = (EditText) findViewById(R.id.et_email);
				et_firstname = (EditText) findViewById(R.id.et_firstname);
				et_lastname = (EditText) findViewById(R.id.et_lastname);
				et_mobile = (EditText) findViewById(R.id.et_mobile);
				et_desc = (EditText) findViewById(R.id.et_description);

				map.put("username", et_username.getText().toString());
				map.put("password", et_password.getText().toString());
				map.put("email", et_email.getText().toString());
				map.put("firstname", et_firstname.getText().toString());
				map.put("lastname", et_lastname.getText().toString());
				map.put("tel", et_mobile.getText().toString());
				map.put("description", et_desc.getText().toString());

				if (validRegistrationForm(cMobile, cEmail, cPw)) {
					ctrl.register(map);

					Toast.makeText(
							RegisterActivity.this,
							"Registration send.\nValidate your email to finish registration",
							Toast.LENGTH_LONG).show();

					RegisterActivity.this.finish();
				} else {
					Toast.makeText(RegisterActivity.this,
							"Registration failed. Please check input",
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	/**
	 * Check if users input is correct
	 * 
	 * @param mobile
	 * @param email
	 * @param pw
	 * @return rather user input is correct or invalid
	 */
	private boolean validRegistrationForm(boolean mobile, boolean email,
			boolean pw) {
		if (mobile && email && pw) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * validate user input in register form
	 * 
	 * @author andres
	 * 
	 */
	// private class InputValidator implements TextWatcher {
	//
	// private EditText editText;
	// private String t;
	// private boolean cEmail;
	// private boolean cMobile;
	//
	// public InputValidator(EditText editText, String toMatch,
	// boolean cEmail, boolean cMobile) {
	// this.editText = editText;
	// t = toMatch;
	// this.cEmail = cEmail;
	// this.cMobile = cMobile;
	// }
	//
	// @Override
	// public void afterTextChanged(Editable s) {
	// switch (editText.getId()) {
	// case R.id.et_email: {
	// if (!email_pattern.matcher(editText.getText().toString())
	// .matches()) {
	// editText.setError("Invalid email");
	// cEmail = false;
	// } else {
	// cEmail = true;
	// }
	// }
	// break;
	//
	// case R.id.et_mobile: {
	// if (!mobile_pattern.matcher(editText.getText().toString())
	// .matches()) {
	// editText.setError("Invalid phone number");
	// cMobile = false;
	// } else {
	// cMobile = true;
	// }
	// }
	// break;
	// }
	//
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before,
	// int count) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// }

}