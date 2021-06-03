package com.dylanc.activityresult.launcher.sample.kotlin.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.dylanc.activityresult.launcher.sample.databinding.ActivityInputTextBinding

class InputTextActivity : AppCompatActivity() {

  private lateinit var binding: ActivityInputTextBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityInputTextBinding.inflate(layoutInflater)
    setContentView(binding.root)
    with(binding) {
      title = intent.getStringExtra(InputTextResultContract.KEY_TITLE)
      tvName.text = intent.getStringExtra(InputTextResultContract.KEY_NAME)
      edtValue.hint = intent.getStringExtra(InputTextResultContract.KEY_HINT)
      edtValue.setText(intent.getStringExtra(InputTextResultContract.KEY_VALUE))
      btnSave.isEnabled = edtValue.text.toString().isNotEmpty()
      edtValue.doAfterTextChanged {
        btnSave.isEnabled = edtValue.text.toString().isNotEmpty()
      }
      btnSave.setOnClickListener { onSave() }
    }
  }

  private fun onSave() {
    val listener = intent.getSerializableExtra(InputTextResultContract.KEY_LISTENER) as OnFilterValueListener?
    val isFilterValue = listener?.onFilterValue(binding.edtValue.text.toString())
    if (isFilterValue != true) {
      val intent = Intent().apply {
        putExtra(InputTextResultContract.KEY_VALUE, binding.edtValue.text.toString())
      }
      setResult(RESULT_OK, intent)
      finish()
    }
  }
}