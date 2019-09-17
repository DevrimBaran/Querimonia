from redminelib import Redmine
from . import settings
from typing import List, Any


class RedmineHandler:
    def __init__(self):
        self.redmine = Redmine(
            "https://stupropm.iao.fraunhofer.de/redmine",
            version="3.45",
            username=settings.USERNAME,
            password=settings.PASSWORD,
        )
        # get config
        self.project_name = "mmk-beschwerdemanagement"
        self.project = self.redmine.project.get(self.project_name)
        self.tracker_story_id = self.project.trackers[3].id
        self.tracker_feature_id = self.project.trackers[1].id
        self.tracker_bug_id = self.project.trackers[0].id
        self.current_sprint_id = self.project.versions[len(self.project.versions) - 1].id
        self.current_sprint_name = self.project.versions[len(self.project.versions) - 1].name


    def get_issues(self) -> List[Any]:
        issues = []
        for issue_type in [self.tracker_bug_id, self.tracker_story_id, self.tracker_feature_id]:
            issues = issues + list(self.redmine.issue.filter(
                project_id=self.project_name,
                fixed_version_id=self.current_sprint_id,
                tracker_id=issue_type,
                sort=":desc"
            ))
        return issues


    def get_issue(self, issue_id: int) -> Any:
        return self.redmine.issue.get(issue_id, include=['children', 'journals', 'watchers'])


    def get_sprint_name(self) -> str:
        return self.current_sprint_name