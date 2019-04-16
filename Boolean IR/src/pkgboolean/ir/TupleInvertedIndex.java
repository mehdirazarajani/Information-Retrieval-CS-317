/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgboolean.ir;

import java.util.ArrayList;

/**
 *
 * @author Mehdi Raza Rajani
 */

public class TupleInvertedIndex {

    public Integer cucumulative_frequency;
    public ArrayList<Integer> posting_list;

    public TupleInvertedIndex(Integer cf, ArrayList<Integer> pl) {
        this.cucumulative_frequency = cf;
        this.posting_list = pl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TupleInvertedIndex tuple = (TupleInvertedIndex) o;
        if (!cucumulative_frequency.equals(tuple.cucumulative_frequency)) return false;
        return posting_list.equals(tuple.posting_list);
    }

    @Override
    public int hashCode() {
        int result = cucumulative_frequency.hashCode();
        result = 31 * result + posting_list.hashCode();
        return result;
    }
    
    @Override
    public String toString(){
        return cucumulative_frequency + " : " + posting_list;
    }
}