/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mehdi Raza Rajani
 */
public class TuplePositionalIndex {
    public Integer cucumulative_frequency;
    public Map<Integer, Set<Integer>> positional_posting_list;

    public TuplePositionalIndex(Integer cf, Map<Integer, Set<Integer>> pl) {
        this.cucumulative_frequency = cf;
        this.positional_posting_list = pl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TupleInvertedIndex tuple = (TupleInvertedIndex) o;
        if (!cucumulative_frequency.equals(tuple.cucumulative_frequency)) return false;
        return positional_posting_list.equals(tuple.posting_list);
    }

    @Override
    public int hashCode() {
        int result = cucumulative_frequency.hashCode();
        result = 31 * result + positional_posting_list.hashCode();
        return result;
    }
    
    @Override
    public String toString(){
        return cucumulative_frequency + " : " + positional_posting_list;
    }
    
}
