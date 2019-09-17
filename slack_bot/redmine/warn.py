from  .handler import RedmineHandler


def _filter_issues_notes(issues_unfiltered):
    # issues without notes
    # issues_urgent_unfiltered = [i for i in issues_unfiltered if i.priority.name == "Urgent"]
    issues_with_notes = []
    issues_without_notes = []
    for i in issues_unfiltered:
        # filter Meetings
        if getattr(i, "category", False):
            if i.category.name == "Meetings":
                continue
        if getattr(i, "journals", False):
            has_notes = False
            for comment in i.journals:
                if getattr(comment, "notes", False) and comment.notes != '':
                    has_notes = True
                    break
            if has_notes is True:
                issues_with_notes.append(i)
            else:
                issues_without_notes.append(i)
    return issues_without_notes


def _build_string(issues_without_notes):
    issues_user = {}
    result_list = []
    result_list.append("Bitte kommentiert noch folgende Tickets:")

    # seperate issues into users
    for i in issues_without_notes:
        assignee = ""
        if getattr(i, "assigned_to", False):
            assignee = i.assigned_to.name
        if assignee in issues_user:
            issues_user[assignee].append(i)
        else:
            issues_user[assignee] = [i]

    # build string
    for user in issues_user:
        result_list.append("*" + user + "*")
        for i in issues_user[user]:
            result_list.append(
                "  - " + "<" + i.url + "| #" + str(i.id) + " " + i.subject + ">"
            )
    return "\n".join(result_list)


def get_warn_message():
    Handler = RedmineHandler()
    issues_unfiltered = Handler.get_issues()
    issues_without_notes = _filter_issues_notes(issues_unfiltered)
    return _build_string(issues_without_notes)
