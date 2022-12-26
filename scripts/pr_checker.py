import base64
import os
import sys
import requests


def check_pull_request(project: str, pr_id: str):
    sonar_token = os.getenv('SONAR_TOKEN')
    if not sonar_token:
        raise Exception(
            "SonarCloud token is not set. Please set SONAR_TOKEN environment variable.")

    sonar_token_base64_bytes = base64.b64encode(f"{sonar_token}:".encode("ascii"))
    sonar_token_base64_string = sonar_token_base64_bytes.decode("ascii")
    url = f"https://sonarcloud.io/api/project_pull_requests/list?project={project}"
    headers = {
        "Authorization": f"Basic {sonar_token_base64_string}"
    }
    response = requests.get(url=url, headers=headers)
    if response.status_code != 200:
        error_msg = f"error response from sonar cloud: {response.status_code}"
        raise Exception(error_msg)
    data = response.json()
    list = [pr for pr in data["pullRequests"] if pr["key"]
            == pr_id]
    if len(list) == 0:
        return
    pr = list[0]
    print(f'{project} pr_number #{pr_id} status', pr['status'])
    
    if pr["status"]["bugs"] > 0:
        raise Exception("Bugs violation")
    
    if pr["status"]["codeSmells"] > 0:
        raise Exception("Code Smells violation")
    
    print("Success")

if __name__ == "__main__":
    project_id = sys.argv[1]
    pr_id = sys.argv[2]
    check_pull_request(project_id, pr_id)
