package com.waseel.achi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import com.waseel.achi.Search.Lists;
import com.waseel.achi.Search.Lists.SearchViewTransformer;

@XmlRootElement
public class SearchResult extends Result {
	
	public SearchResult(Outcome outcome, String message) {
		super(Result.Outcome.SUCCESS, "");
	}
	
	public SearchResult(Collection<?> results, int nextPageToken,
			int maxFetchSize, SearchViewTransformer transformer, UriInfo uri) {
		super(Result.Outcome.SUCCESS, "");
		this.uri = uri;
		this.results = Lists.transform(results, transformer);
		int factor = (size() / maxFetchSize);
		this.nextPageToken = factor * (nextPageToken + maxFetchSize);
	}
	
	private UriInfo uri;
	private Object results;
	private int nextPageToken;
	
	@SuppressWarnings("unchecked")
	private int size() {
		int size = 0;
		if (results instanceof Collection) {
			size = ((Collection<?>) results).size();
		} else if (results instanceof Map) {
			for (Collection<?> c : ((Map<?, List<?>>) results).values()) {
				size += c.size();
			}
		}
		return size;
	}

	public Object getResults() {
		return results;
	}
	
	@Override
	public String getOutcome() {
		if (size() <= 0) {
			return Result.Outcome.FAILURE.value();
		}
		return outcome;
	}

	@Override
	public String getMessage() {
		if (size() <= 0) {
			return "Sorry! Found provider(s) 0.";
		}
		return message;
	}

}
