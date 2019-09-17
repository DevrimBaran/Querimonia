import pypandoc
import pdb
from datetime import date
from .handler import RedmineHandler


def _filter_issues_review(issues_unfiltered):
    return [i for i in issues_unfiltered if i.category.name != "Meetings"]


def _filter_issues_weekly(issues_unfiltered):
    issues_urgent = [i for i in issues_unfiltered if i.priority.name == "Urgent"]
    issues_notes_unfiltered = [i for i in issues_unfiltered if i.priority.name != "Urgent"]
    # search if issue has note
    issues_notes = []
    for i in issues_notes_unfiltered:
        if getattr(i, "journals", False):
            has_notes = False
            for comment in i.journals:
                if getattr(comment, "notes", False):
                    if comment.notes != '':
                        has_notes = True
                        break
            if has_notes is True:
                issues_notes.append(i)
    issues = list(issues_urgent) + list(issues_notes)
    return issues

def _get_notes(issue):
    tmp_notes = []
    if getattr(issue, "journals", False):
        for comment in issue.journals:
            if getattr(comment, "notes", False) and comment.notes != "":
                tmp_notes.append(comment.notes + "\n")
    return tmp_notes

def _build_string(issues, current_sprint_name):
    present_list = []
    present_list.append("% " + current_sprint_name)
    present_list.append("% " + date.today().isoformat())
    for i in issues:
        # heading
        issue_title = "[#" + str(i.id) + " " + i.subject + "](" + i.url + ")"
        sub_title = "## " + issue_title + ", "
        present_list.append("# " + issue_title + "\n")

        # body
        # desc
        if getattr(i, "description", False):
            present_list.append(sub_title + "Description" + "\n")
            # heuristic for the amount of text on the slide
            if len(i.description) > 531:
                present_list.append(i.description[0:530] + "..." + "\n")
            else:
                present_list.append(i.description + "\n")
        # infos
        assignee = ""
        category_name = ""
        done_ratio = str(i.done_ratio) + "/100"
        if getattr(i, "assigned_to", False):
            assignee = i.assigned_to.name
        if getattr(i, "category", False):
            category_name = i.category.name
        present_list.append("\n### " + done_ratio + " | " + assignee + " | " + category_name + "\n")
        # subtasks
        subtasks_list = []
        if getattr(i, "children", False):
            subtasks = ""
            for subtask in i.children:
                subtasks_list.append(subtask)
                subtasks = subtasks + subtask.subject + ", "
            present_list.append("**Subtasks**: " + subtasks[0:-2] + "\n")
        # notes
        tmp_notes = _get_notes(i)
        if len(tmp_notes) > 0:
            present_list.append(sub_title + "Notes" + "\n")
            present_list.extend(tmp_notes)
        # subtask notes
        for subtask in subtasks_list:
            tmp_notes = _get_notes(subtask)
            if len(tmp_notes) > 0:
                present_list.extend(tmp_notes)

    return "\n".join(present_list)


def _create_pdf(present):
    pypandoc.convert_text(
        present, "beamer", format="md", extra_args=['-V', 'theme:metropolis'], outputfile="present.pdf"
    )


def create_presentation():
    Handler = RedmineHandler()
    issues_unfiltered = Handler.get_issues()
    issues = _filter_issues_review(issues_unfiltered)
    current_sprint_name = Handler.get_sprint_name()
    present = _build_string(issues, current_sprint_name)
    _create_pdf(present)