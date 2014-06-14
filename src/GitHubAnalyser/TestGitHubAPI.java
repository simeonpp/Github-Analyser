package GitHubAnalyser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.RepositoryService;

public class TestGitHubAPI {
	
	RepositoryService service = new RepositoryService();
	
	private ArrayList<String> gitHubRepos = new ArrayList<String>();
	private ArrayList<Integer> gitHubReposId = new ArrayList<Integer>();
	private ArrayList<String> contributors = new ArrayList<String>();
	private ArrayList<Integer> contributorsCommits = new ArrayList<Integer>();
	
	protected ArrayList<String> getGitHubRepos() { return this.gitHubRepos; }
	protected ArrayList<Integer> getGitHubReposId() { return this.gitHubReposId; }
	protected ArrayList<String> getContributors() { return this.contributors; }
	protected ArrayList<Integer> getContributorsCommits() { return this.contributorsCommits; }
	
	
	protected void setRepositories() {		
		service.getClient().setOAuth2Token( Variables.authToken );
		
		this.gitHubRepos.clear();
		this.gitHubReposId.clear();
		
		ArrayList<String> tempStringList = new ArrayList<String>( Arrays.asList( "Repo1", "Repo2", "Repo3", "Repo4", "Repo5", "Repo8", "Repo9", "Repo10", "Repo11" ));
		ArrayList<Integer> tempIntegerList = new ArrayList<Integer>( Arrays.asList( 1859, 2859, 3859, 4859, 5859, 8859, 9859, 10859, 11859 ));
		
		for (int i = 0; i < tempStringList.size(); i++ ) {
			this.gitHubRepos.add( tempStringList.get(i) );
			this.gitHubReposId.add( tempIntegerList.get(i) );
		}
		
	}
	
	
	protected String getRepoOwner(String repository) {
		String owner = "Unknown";
		switch (repository) {
		case "Repo1": owner = "User1";
		break;
		case "Repo2": owner = "User1";
		break;
		case "Repo3": owner = "User1";
		break;
		case "Repo4": owner = "User2";
		break;
		case "Repo5": owner = "User2";
		break;
		case "Repo8": owner = "User4";
		break;
		case "Repo9": owner = "User4";
		break;
		case "Repo10": owner = "User5";
		break;
		case "Repo11": owner = "User5";
		break;
		}
		return owner;
	}

	
	protected void setRepoContributors(int repoId, String repoOwner, String repository) throws IOException {
		
		this.contributors.clear();	
		this.contributorsCommits.clear();
		
		switch (repoId) {
		case 1859: 
		break;
		case 2859: contributors.add( "User4" ); contributorsCommits.add( 1 ); contributors.add( "User2" ); contributorsCommits.add( 2 ); contributors.add( "User3" ); contributorsCommits.add( 4 );
		break;
		case 3859: 
		break;
		case 4859: contributors.add( "User1" ); contributorsCommits.add( 5 ); contributors.add( "User3" ); contributorsCommits.add( 3 );
		break;
		case 5859: 
		break;
		case 8859: contributors.add( "User1" ); contributorsCommits.add( 8 ); contributors.add( "User2" ); contributorsCommits.add( 20 );
		break;
		case 9859: contributors.add( "User3" ); contributorsCommits.add( 7 );
		break;
		case 10859: contributors.add( "User1" ); contributorsCommits.add( 2 ); contributors.add( "User3" ); contributorsCommits.add( 7 );
		break;
		case 11859: 
		break;
		}
	}	
	
}
