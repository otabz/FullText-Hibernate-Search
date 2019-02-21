package com.waseel.achi.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

@Entity
@Table(name="achi_code")
@Indexed
@NamedQueries(value = { 
		@NamedQuery(name = "Achi.Codes", query = "SELECT a FROM AchiCode a ORDER BY a.code asc"),
		@NamedQuery(name = "Achi.Code", query = "SELECT a FROM AchiCode a WHERE a.code = :code"),
		@NamedQuery(name = "Achi.Descriptions", query = "SELECT a FROM AchiCode a ORDER BY a.description asc")
		})
public class AchiCode implements Serializable, AbstractCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1054627469791785082L;
	
	@Fields({
		  @Field(name = "edgeNGramCode", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		@Field(name = "code_", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer"))
		})
	private String code;
	@Fields({
		  @Field(name = "standardDescription", index = Index.YES, store = Store.NO,
		analyze = Analyze.YES, analyzer = @Analyzer(definition = "standardAnalyzer"))
		})
	private String description;
	private String mode;
	private List<HospitalCode> hospitaCodes;
	
	@Id
	@Column(name="code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(mappedBy = "achiCode", 
	        cascade = CascadeType.ALL, 
	        orphanRemoval = true)
	//@IndexedEmbedded
	public List<HospitalCode> getHospitaCodes() {
		return hospitaCodes;
	}
	public void setHospitaCodes(List<HospitalCode> hospitaCodes) {
		this.hospitaCodes = hospitaCodes;
	}
	
	@Column(name="mode")
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String _hospitalCode() {
		return "";
	}
	@Override
	public String _hospitalDesc() {
		return "";
	}
	@Override
	public String _achiCode() {
		return this.getCode();
	}
	@Override
	public String _achiDesc() {
		return this.getDescription();
	}
	@Override
	public String _achiMode() {
		return this.getMode();
	}
	
	public void addHospitalCode(HospitalCode code) {
        hospitaCodes.add(code);
        code.setAchiCode(this);
    }
 
//    public void removeComment(PostComment comment) {
//        comments.remove(comment);
//        comment.setPost(null);
//    }
}
