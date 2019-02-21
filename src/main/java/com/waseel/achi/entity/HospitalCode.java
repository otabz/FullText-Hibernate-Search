package com.waseel.achi.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="hospital_code")
@Indexed
@NamedQueries(value = { 
		@NamedQuery(name = "Hospital.Codes", query = "SELECT c FROM HospitalCode c ORDER BY c.code asc"),
		@NamedQuery(name = "Hospital.Descriptions", query = "SELECT c FROM HospitalCode c ORDER BY c.description asc")
		})

@AnalyzerDefs({

@AnalyzerDef(name = "autocompleteEdgeAnalyzer",

// Split input into tokens according to tokenizer
tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),

filters = {
 // Normalize token text to lowercase, as the user is unlikely to
 // care about casing when searching for matches
 @TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
   @Parameter(name = "pattern",value = "([^a-zA-Z0-9\\.])"),
   @Parameter(name = "replacement", value = " "),
   @Parameter(name = "replace", value = "all") }),
 @TokenFilterDef(factory = LowerCaseFilterFactory.class),
 //@TokenFilterDef(factory = StopFilterFactory.class),
 // Index partial words starting at the front, so we can provide
 // Autocomplete functionality
 @TokenFilterDef(factory = EdgeNGramFilterFactory.class, params = {
   @Parameter(name = "minGramSize", value = "1"),
   @Parameter(name = "maxGramSize", value = "50") }) }
),

@AnalyzerDef(name = "autocompleteNGramAnalyzer",

//Split input into tokens according to tokenizer
tokenizer = @TokenizerDef(factory =  StandardTokenizerFactory.class),

filters = {
// Normalize token text to lowercase, as the user is unlikely to
// care about casing when searching for matches
@TokenFilterDef(factory = WordDelimiterFilterFactory.class),
@TokenFilterDef(factory = LowerCaseFilterFactory.class),
@TokenFilterDef(factory = NGramFilterFactory.class, params = {
@Parameter(name = "minGramSize", value = "3"),
@Parameter(name = "maxGramSize", value = "5") }),
@TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
@Parameter(name = "pattern",value = "([^a-zA-Z0-9\\.])"),
@Parameter(name = "replacement", value = " "),
@Parameter(name = "replace", value = "all") })
}),

@AnalyzerDef(name = "standardAnalyzer",

//Split input into tokens according to tokenizer
tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),

filters = {
// Normalize token text to lowercase, as the user is unlikely to
// care about casing when searching for matches
@TokenFilterDef(factory = WordDelimiterFilterFactory.class),
@TokenFilterDef(factory = LowerCaseFilterFactory.class),
@TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
@Parameter(name = "pattern", value = "([^a-zA-Z0-9\\.])"),
@Parameter(name = "replacement", value = " "),
@Parameter(name = "replace", value = "all") }),
@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
	      @Parameter(name = "language", value = "English")
	    })
})
})
public class HospitalCode  implements Serializable, AbstractCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1888565637043570693L;
	
	@Fields({
		  @Field(name = "edgeNGramCode", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		 @Field(name = "code_", index = Index.YES, store = Store.NO,
			analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer"))
		})
	private String code;
	@Fields({
		@Field(name = "edgeNGramDescription", index = Index.YES, store = Store.NO,
				analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "standardDescription", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer"))
		})
	private String description;
	private AchiCode achiCode;
	
	@Id
	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="achi_code")
	//@ContainedIn
	public AchiCode getAchiCode() {
		return achiCode;
	}
	public void setAchiCode(AchiCode achiCode) {
		this.achiCode = achiCode;
	}
	@Override
	public String _hospitalCode() {
		return this.getCode();
	}
	@Override
	public String _hospitalDesc() {
		return this.getDescription();
	}
	@Override
	public String _achiCode() {
		return this.getAchiCode().getCode();
	}
	@Override
	public String _achiDesc() {
		return this.getAchiCode().getDescription();
	}
	@Override
	public String _achiMode() {
		return this.getAchiCode().getMode();
	}
	
	public void addAchiCode(String code) {
		this.achiCode = new AchiCode();
		this.achiCode.setCode(code);
	}
	
	
}
