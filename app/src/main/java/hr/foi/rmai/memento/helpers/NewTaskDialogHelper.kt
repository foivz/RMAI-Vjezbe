package hr.foi.rmai.memento.helpers

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import hr.foi.rmai.memento.R
import hr.foi.rmai.memento.entities.Task
import hr.foi.rmai.memento.entities.TaskCategory

class NewTaskDialogHelper(val view:View) {
    private val spinner = view.findViewById<Spinner>(R.id.spn_new_task_dialog_category)
    private val dateSelection = view.findViewById<EditText>(R.id.et_new_task_dialog_date)
    private val timeSelection = view.findViewById<EditText>(R.id.et_new_task_dialog_time)

    fun populateSpinner(categories : List<TaskCategory>) {

    }

    fun buildTask() : Task? {
        return null
    }
}