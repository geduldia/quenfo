package quenfo.de.uni_koeln.spinfo.categorization.data;

import java.util.Set;

/**
 * @author geduldia
 * 
 * Kategorisierbares Objekt (Tool oder Kompetenz)
 * 
 *
 */
public class Entity {
	
	//nur bei einigen AMS-Kompetenzen
	private Set<String> synonyms;
	private String lemma;
	//nur bei AMS-Kopetenzen
	private String string;
	
	private boolean validated;
	
	private Set<Category> categories;
	
	
	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public Set<Category> getCategories(){
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public Entity(String lemma){
		this.lemma = lemma;
	}
	
	public void setLemma(String lemma){
		this.lemma = lemma;
	}

	public Set<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getLemma() {
		return lemma;
	}
	
	@Override
	public boolean equals(Object o){
		return this.lemma.equals(((Entity) o).getLemma());
	}
	
	@Override
	public int hashCode(){
		return lemma.hashCode();
	}

	public void setString(String string) {
		this.string = string;
	}
	public String getString(){
		return this.string;
	}
	
	@Override
	public String toString(){
		return lemma+ "("+validated+")";
	}

}
