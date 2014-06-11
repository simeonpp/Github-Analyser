package GitHubAnalyser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GitHubAPI {
	
	RepositoryService service = new RepositoryService();
	
	private ArrayList<String> contributors = new ArrayList<String>();
	private ArrayList<Integer> contributorsCommits = new ArrayList<Integer>();
	
	protected ArrayList<String> getArrayListOfContributors() { return this.contributors; }
	protected ArrayList<Integer> getArrayListOfContributorsCommits() { return this.contributorsCommits; }
	
	
	protected ArrayList<String> getRepositories() {		
		service.getClient().setOAuth2Token( Variables.authToken );
		
		ArrayList<String> gitHubRepos = new ArrayList<String>( Arrays.asList("Repo1", "Repo2", "Repo3", "Repo4", "Repo5", "Repo8", "Repo9", "Repo10", "Repo11" ));	
		return gitHubRepos;
	}
	
	
	protected String getRepoOwner(String repository) {
		service.getClient().setOAuth2Token( Variables.authToken );
		
		String owner = "Unknown";
		return owner;
	}

	
	protected void getRepoContributors(String repoId, String repoOwner, String repository) throws IOException {
		service.getClient().setOAuth2Token( Variables.authToken );
		
		this.contributors.clear();	
		this.contributorsCommits.clear();
		
		for ( Contributor repo : service.getContributors(RepositoryId(repoId, repoOwner, repository), true))
		{
			if ( repo.getLogin() != null ) {					// check if the contributor is null (very unlikely to happen, but just in case)		
				contributors.add( repo.getLogin() );
				contributorsCommits.add( repo.getContributions() );
			} else { }
		}
	}	
	
	
		
	/**
	 * Method to get RepositoryId 
	 * @param userId String value
	 * @param username String value
	 * @param repository String value
	 * @return RepositoryId
	 */
	private static IRepositoryIdProvider RepositoryId(String userId, String username, String repo) {
		if (userId == null || userId.length() == 0)
			return null;
		
		String owner = username;
		String name = repo;
		for (String segment : userId.split("/")) //$NON-NLS-1$
			if (segment.length() > 0)
				if (owner == null)
					owner = segment;
				else if (name == null)
					name = segment;
				else
					break;

		return owner != null && owner.length() > 0 && name != null
				&& name.length() > 0 ? new RepositoryId(owner, name) : null;
	}
	
}
