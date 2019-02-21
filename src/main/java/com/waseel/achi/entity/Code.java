package com.waseel.achi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
//import org.hibernate.search.annotations.Facet;
//import org.hibernate.search.annotations.FacetEncodingType;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 * Code entity. @author MyEclipse Persistence Tools
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
//@Entity(name="code")
//@Indexed
@NamedNativeQueries({
@NamedNativeQuery(name = "Code.findOneDistinct", query = "select distinct(icd10), c.adesc, c.type from code as c where c.icd10 like :icd10  order by c.icd10 asc",
resultSetMapping = "DistinctCodes"),
@NamedNativeQuery(name = "Code.findAllDistinct", query = "select distinct(icd10), c.adesc, c.type from code as c order by c.icd10 asc",
resultSetMapping = "DistinctCodes")
})
@NamedQueries({
@NamedQuery(name = "Code.findOneAchi", query = "SELECT c FROM code c WHERE c.icd10 = :icd10"),
@NamedQuery(name = "Code.findAllCodes", query = "SELECT c FROM code c ORDER BY c.code asc"),
@NamedQuery(name = "Code.findAllDescs", query = "SELECT c FROM code c ORDER BY c.desc asc"),
//@NamedQuery(name = "Code.findAllGroupBy", query = "SELECT NEW Code(DISTINCT(c.icd10), c.adesc) FROM code c ORDER BY c.icd10 asc"),
@NamedQuery(name = "Code.findSubCodes", query = "SELECT c FROM code c WHERE c.icd10 = :icd10 ORDER BY c.timeStamp asc")})
/*@FullTextFilterDefs({@FullTextFilterDef(name="payerFilter", impl = PayerFilterFactory.class),
		@FullTextFilterDef(name="categoryFilter", impl=CategoryFilterFactory.class),
		@FullTextFilterDef(name="openFilter", impl=OpenFilterFactory.class),
		@FullTextFilterDef(name="cityFilter", impl=CityFilterFactory.class)})*/

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

// Split input into tokens according to tokenizer
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

// Split input into tokens according to tokenizer
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

/*@SqlResultSetMapping(name = "DistinctAchiCodes", entities={
		@EntityResult(entityClass = Code.class,
				fields = {
			@FieldResult(column = "icd10", name = "icd10"),
			@FieldResult(column = "adesc", name = "adesc"),
			@FieldResult(column = "type", name = "type"),
			@FieldResult(column = "code", name = "code")
		})
})*/
@SqlResultSetMapping(
	    name = "DistinctCodes",
	    classes = @ConstructorResult(columns = { 
	    		@ColumnResult(name="icd10"),
	    		@ColumnResult(name="adesc"),
	    		@ColumnResult(name="type")}, targetClass = Code.class)
	)
public class Code implements java.io.Serializable {

	// Fields
	@Fields({
		  @Field(name = "code_", index = Index.YES, store = Store.YES,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer")),
		  @Field(name = "edgeNGramCode", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "nGramCode", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteNGramAnalyzer"))
		})
	private String code;
	
	@Fields({
		  @Field(name = "desc_", index = Index.YES, store = Store.YES,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer")),
		  @Field(name = "edgeNGramDesc", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "nGramDesc", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteNGramAnalyzer"))
		})
	private String desc;
	
	@Fields({
		  @Field(name = "achi_", index = Index.YES, store = Store.YES,
		analyze = Analyze.NO, analyzer = @Analyzer(definition = "standardAnalyzer")),
		  @Field(name = "edgeNGramAchi", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "nGramAchi", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteNGramAnalyzer"))
		})
	//@Facet(forField="achi_", encoding=FacetEncodingType.STRING)
	private String icd10;
	private String type;
	
	@Fields({
		  @Field(name = "adesc_", index = Index.YES, store = Store.YES,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer")),
		  @Field(name = "edgeNGramADesc", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "nGramADesc", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteNGramAnalyzer"))
		})
	private String adesc;
	private Date timeStamp;

	// Constructors

	/** default constructor */
	public Code() {
	}

	/** minimal constructor */
	public Code(String code) {
		this.code = code;
	}
	
	public Code(String icd10, String adesc, String type) {
		this.icd10 = icd10;
		this.adesc = adesc;
		this.type = type;
	}

	/** full constructor */
	public Code(String code, String desc, String icd10, String type, String adesc) {
		this.code = code;
		this.desc = desc;
		this.icd10 = icd10;
		this.type = type;
		this.adesc = adesc;
	}

	// Property accessors
	@Id
	@Column(name = "code", unique = true, nullable = false, length = 20)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "`desc`", length = 250)
	public String getDesc() {
		return this.desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Column(name = "icd10", length = 20)
	public String getIcd10() {
		return this.icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}

	@Column(name = "type", length = 45)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "adesc", length = 250)
	public String getAdesc() {
		return this.adesc;
	}
	
	public void setAdesc(String adesc) {
		this.adesc = adesc;
	}
	
	@Column(name="timestamp", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
	@Override
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		} else if(!(other instanceof Code)) {
			return false;
		} else if(((Code)other).icd10 == null) {
			return false;
		} else if(((Code)other).icd10.equals(this.icd10)) {
			return true;
			
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + (this.icd10 == null ? 0 : this.icd10.hashCode());
		//hashCode = 31 * hashCode + (this.adesc == null ? 0 : this.adesc.hashCode());
		return hashCode;
	}
	
	@Override
	public String toString() {
		return "icd10: ".concat(this.icd10 == null ? " " : this.icd10).
				concat(" adesc: ").concat(this.adesc == null ? " " : this.adesc)
				.concat(" code: ").concat(this.code == null ? " " : this.code)
				.concat(" desc: ").concat(this.desc == null ? " " : this.desc)
				.concat(" type: ").concat(this.type == null ? " " : this.type);
	}
}