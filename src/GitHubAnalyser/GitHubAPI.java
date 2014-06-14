package GitHubAnalyser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GitHubAPI {
	
	RepositoryService service = new RepositoryService();
	
	private ArrayList<String> gitHubRepos = new ArrayList<String>();
	private ArrayList<Integer> gitHubReposId = new ArrayList<Integer>();
	private ArrayList<String> gitHubReposOwner = new ArrayList<String>();
	private ArrayList<String> contributors = new ArrayList<String>();
	private ArrayList<Integer> contributorsCommits = new ArrayList<Integer>();
	
	protected ArrayList<String> getGitHubRepos() { return this.gitHubRepos; }
	protected ArrayList<Integer> getGitHubReposId() { return this.gitHubReposId; }
	protected ArrayList<String> getGitHubReposOwner() { return this.gitHubReposOwner; }
	protected ArrayList<String> getContributors() { return this.contributors; }
	protected ArrayList<Integer> getContributorsCommits() { return this.contributorsCommits; }
	
	
	protected void setReposUserIds() throws IOException {		
		service.getClient().setOAuth2Token( Variables.authToken );
		
		this.gitHubRepos.clear();
		this.gitHubReposId.clear();
		this.gitHubReposOwner.clear();
		
		List<SearchRepository> searchRepos = searchForReposotories();
        
        for ( int i = 0; i < searchRepos.size(); i++){
        	String tempString = searchRepos.get(i).toString();
        	String[] splitTempString = tempString.split("/");
        	for ( int j = 0; j < splitTempString.length; j ++){
        		if ( j % 2 == 0 ) {
        			this.gitHubReposOwner.add(splitTempString[j]);
        		} else {
        			this.gitHubRepos.add(splitTempString[j]);
        		}
        	}
        }     
       setRepoIds( this.gitHubReposOwner, this.gitHubRepos );
	}
	
	
	
	protected void setRepoContributors(int repoId, String repoOwner, String repository) throws IOException {
		
		service.getClient().setOAuth2Token( Variables.authToken );
		
		this.contributors.clear();	
		this.contributorsCommits.clear();
		
		for ( Contributor repo : service.getContributors(RepositoryId( Variables.convertIntegerToString(repoId), repoOwner, repository), true))
		{
			if ( repo.getLogin() != null ) {					// check if the contributor is null (very unlikely to happen, but just in case)		
				contributors.add( repo.getLogin() );
				contributorsCommits.add( repo.getContributions() );
			} else { }
		}
	}	
	

	
	
	
	
	
	
	
	
	
	
	
	private List<SearchRepository> searchForReposotories() {
		Map<String, String> searchQuery = new HashMap<String, String>();
		searchQuery.put("keyword","Java");  
		
        List<SearchRepository> searchRes = null;
        try {
            searchRes = service.searchRepositories(searchQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }	
        return searchRes;
	}
	
	
	private void setRepoIds(ArrayList<String> owners, ArrayList<String> repositories) throws IOException {
		for (int i = 0; i < owners.size(); i ++){
			long longId = service.getRepository(owners.get(i), repositories.get(i)).getId();
			int id = (int) longId;
			this.gitHubReposId.add(id);
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
