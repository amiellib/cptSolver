import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.*;
public class Ex1
{
	static DecimalFormat df= new DecimalFormat("0.00000");
	static String text="";
	static int add_counter=0;
	static int mul_counter=0;
	static Hashtable<String, Node> nodes = new Hashtable<String, Node>();
	/*
  here algo 1 will run
	 */
	static public String algo_1(String[] queries,  ArrayList<String> hidden ,Hashtable <String,String> un_hidden,String var ,String var_value )
	{
		boolean bool=false;
		Node check=nodes.get(var);
		double sum_true=0,sum_false=0;
		Hashtable<String, Node> nodes_copy=new   Hashtable<String, Node>(nodes);
		for(int m=0;m<queries.length;m++)
		{
			nodes_copy.remove(queries[m].substring(0,queries[m].indexOf(":")));
		}
		if(check.get_parents().size()==queries.length-1)
		{
			String str="";
			//fast end if queries is exacly same to a given info in cpt
			for(int i=0;i<queries.length&&!bool;i++)
			{
				if(check.get_parents().contains(queries[i].substring(0,queries[i].indexOf(":")))||queries[i].substring(0,queries[i].indexOf(":")).equals(var))
				{
					str+=queries[i];
				}
				else
					bool=true;
			}
			if(!bool)
			{
				return df.format(Math.round((check.get_value(str)) * 100000d) / 100000d);
			}
		}
		for (int i = 0; i<nodes.get(var).get_value().length; i++)
		{
			un_hidden.put(var,nodes.get(var).get_value()[i]);
			//do all funcions and return sum
			double sum = get_options_odds(un_hidden, nodes_copy, 0);
			//sum the info
			if(var_value.equals(nodes.get(var).get_value()[i]))
			{
				if(sum_true==0.0)
				{
					sum_true=sum;
				}
				else
				{
					add_counter++;
					sum_true+=sum;
				}
			}
			else
			{
				if(sum_false==0.0)
				{
					sum_false=sum;
				}
				else
				{
					add_counter++;
					sum_false+=sum;
				}
			}
		}
		add_counter++;
		//nice print
		double return_sum_nice=sum_true/(sum_false+sum_true);
		return_sum_nice=(double)Math.round((return_sum_nice) * 100000d) / 100000d;
		return df.format(return_sum_nice);
	}
	/*
  get all options  and make it odds
	 */
	static public double get_options_odds(Hashtable<String, String> known , Hashtable<String, Node> nodes_copy, double odds)
	{
		ArrayList<Hashtable<String, String>> looking_for = new ArrayList<Hashtable<String, String>>();
		looking_for = rec_look_for(looking_for, known, new ArrayList<Node>(nodes_copy.values()));
		for (Hashtable<String, String> one_at_a_time : looking_for)
		{
			Set<String> keys = nodes.keySet();
			//Obtaining iterator over set entries
			Iterator<String> itr = keys.iterator();
			//Displaying Key and value pairs
			double mult = 1;
			while (itr.hasNext())
      {
				// Getting Key
				String var = itr.next();
				if (mult==1)
				{
					mult=nodes.get(var).get_odds(one_at_a_time);
				}
				else
				{
					mul_counter ++;
					mult *= nodes.get(var).get_odds(one_at_a_time);
				}
			}
			if (odds==0)
			{
				odds = mult;
			}
			else
			{
				add_counter++;
				odds += mult;
			}
		}
		return odds;
	}
	/*
recursev funcion to get array list of all hashtables
	 */
	static public ArrayList<Hashtable<String, String>> rec_look_for(ArrayList<Hashtable<String, String>> looking_for, Hashtable<String, String> known, ArrayList<Node> known_arr_list)
	{
		if (known_arr_list.isEmpty())
		{
			looking_for.add(known);
			return looking_for;
		}
		else
		{
			Node temp=known_arr_list.remove(0);
			for (int n = 0; n<temp.get_value().length; n++)
			{
				Hashtable<String, String> new_base = make_new_one(known);
				new_base.put(temp.get_var(),temp.get_value()[n]+" ");
				looking_for=rec_look_for(looking_for,new_base,new ArrayList<Node>(known_arr_list));
			}
		}
		return looking_for;
	}
	/*
  deep copy of the nodes to hashtable<String,String>
	 */
	static public Hashtable<String,Node> make_new_one_node(Hashtable<String, Node> need_to_copy)
	{
		Hashtable<String, Node> new_one = new Hashtable<String, Node>(need_to_copy.size());
		Set<String> keys = need_to_copy.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext()) {
			String var = itr.next();
			new_one.put(var,new Node(var,need_to_copy.get(var).get_value(),new ArrayList<String>(need_to_copy.get(var).get_parents()), new Hashtable<String,Double> (need_to_copy.get(var).Cpt)));
		}
		return new_one;
	}
	/*
    deep copy of of hashtable keys
	 */
	static public Hashtable<String,String> make_new_one(Hashtable<String, String> need_to_copy)
	{
		Hashtable<String, String> new_one = new Hashtable<String, String>(need_to_copy.size());
		Set<String> keys = need_to_copy.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext()) {
			String var = itr.next();
			new_one.put(var, need_to_copy.get(var));
		}
		return new_one;
	}
	/*
  here i run algo 2
	 */
	public static String algo_2(String[] queries,  ArrayList<String> hidden ,Hashtable <String,String> un_hidden,String var ,String var_value )
	{
		//fast end just like in algo_1
		Double odds=0.0;
		boolean bool=false;
		Node check=nodes.get(var);
		if(check.get_parents().size()==queries.length-1)
		{
			String str="";
			for(int i=0;i<queries.length&&!bool;i++)
			{
				if(check.get_parents().contains(queries[i].substring(0,queries[i].indexOf(":")))||queries[i].substring(0,queries[i].indexOf(":")).equals(var))
				{
					str+=queries[i];
				}
				else
					bool=true;
			}
			if(!bool)
			{
				return df.format(check.get_value(str));
			}
		}
		//eliminion
		Hashtable<String, Node> nodes_copy =new Hashtable<String, Node>(make_new_one_node(nodes));
		for(int i=0;i<queries.length;i++)
		{
			Set<String> keys = nodes.keySet();
			for(String node_var: keys)
			{
				if(!queries[i].substring(0,queries[i].indexOf(":")).equals(var))
					nodes_copy.get(node_var).eliminion(queries[i]);
			}
		}
		//delet one line CPT from the Node
		Set<String> keys = new HashSet<String> (nodes_copy.keySet());
		HashSet<String>ancestor=new HashSet<String>();
		for(int i=0;i<queries.length;i++)
		{
			String temp=queries[i].substring(0,queries[i].indexOf(":"));
			ancestor=rec_find_ancestor(nodes_copy,temp,ancestor);
		}
		//delete all values that i do not use becuse i eliminion
		for(String node_var: keys)
			if(nodes_copy.get(node_var).Cpt.size()<2 || !ancestor.contains(node_var))
			{
				nodes_copy.remove(node_var);
			}
		int  hidden_size=hidden.size();
		for(int j=0 ;j<hidden_size;j++)
		{
			if(!ancestor.contains(hidden.get(j)))
			{
				hidden.remove(hidden.get(j));
				hidden_size--;
				j--;
			}
		}
		//join Cpt by abc order
		Collections.sort(hidden);
		ArrayList<Hashtable<String,Double>> cpt=join_cpt(nodes_copy,ancestor,var_value,var,hidden);
		while(cpt.size()!=1)
		{
			HashSet<String> cpt_set=new  HashSet<String>();
			HashSet<String> cpt_set_min=new  HashSet<String>();
			int index_i=0,index_j=1,sum=-1,asci_min=-1,asci_i,asci_j;
			for(int i=0;i<cpt.size()-1;i++)
			{
				String cpt_i=cpt.get(i).keySet().iterator().next();
				String[] temp_i=cpt_i.split(" ");
				asci_i=0;
				for(int j=i+1;j<cpt.size();j++)
				{
					String cpt_j=cpt.get(j).keySet().iterator().next();
					String[] temp_j=cpt_j.split(" ");
					asci_j=0;
					for(String sub_j:temp_j)
					{
						cpt_set.add(sub_j.substring(0,sub_j.indexOf(":")));
						for(int o=0;o<sub_j.substring(0,sub_j.indexOf(":")).length();o++)
							asci_j+=(int)sub_j.substring(0,sub_j.indexOf(":")).charAt(o);
					}
					for(String sub_i:temp_i)
					{
						cpt_set.add(sub_i.substring(0,sub_i.indexOf(":")));
						if(asci_i!=0)
							for(int o=0;o<sub_i.substring(0,sub_i.indexOf(":")).length();o++)
								asci_i+=(int)sub_i.substring(0,sub_i.indexOf(":")).charAt(o);
					}
					int temp_sum=1;
					for(String vAr:cpt_set)
					{
						temp_sum*=nodes_copy.get(vAr).get_value().length;
					}
					if(sum>temp_sum||sum==-1)
					{
						index_i=i;
						index_j=j;
						sum=temp_sum;
						asci_min=asci_i+asci_j;
						cpt_set_min.clear();
						for(String  var_str_for_end:cpt_set )
							cpt_set_min.add(var_str_for_end);
					}
					else
					{
						if(sum==temp_sum&&asci_i+asci_j<asci_min)
						{
							index_i=i;
							index_j=j;
							sum=temp_sum;
							asci_min=asci_i+asci_j;
							cpt_set_min.clear();
							for(String  var_str_for_end:cpt_set )
								cpt_set_min.add(var_str_for_end);
						}
					}
					cpt_set.clear();
				}
			}
			//join cpt here
			Hashtable<String,Double> copy_i=new Hashtable<String,Double>(cpt.get(index_i));
			Hashtable<String,Double> copy_j=new Hashtable<String,Double>(cpt.get(index_j));
			cpt.remove(Math.max(index_i,index_j));//i know i can put here j just to make sure
			cpt.remove(Math.min(index_i,index_j));///i know i can put here i just to make sure
			cpt.add(join_Cpt(copy_i,copy_j,nodes_copy, cpt_set_min));
		}
		Double sum_over_all=0.0,sum_value_only=0.0;
		//nirmoul
		for(Hashtable<String,Double> nirmoul:cpt)
		{
			for(String nirmul_str:nirmoul.keySet())
			{
				if(nirmul_str.contains(var+":"+var_value+" "))
					sum_value_only+=nirmoul.get(nirmul_str);
				else
					sum_over_all+=nirmoul.get(nirmul_str);
			}
		}
		//nice print
		odds=sum_value_only/(sum_over_all+sum_value_only);
		add_counter++;
		double nice_odds=(double)Math.round((odds) * 100000d) / 100000d;
		return df.format(nice_odds);
	}
	/*
    join cpt of the nodes by comman  hidden
	 */
	public static   ArrayList<Hashtable<String,Double>>  join_cpt(Hashtable<String,Node>nodes_copy ,HashSet<String>ancestor,String var_value,String vAr,ArrayList <String>hidden)
	{
		ArrayList<Hashtable<String,Double>> cpt=new  ArrayList<Hashtable<String,Double>>();
		ArrayList<Hashtable<String,Double>> copy_cpt=new  ArrayList<Hashtable<String,Double>>();
		Set<String> keys = nodes_copy.keySet();
		for(String var:keys)
		{
			if(ancestor.contains(var))
				cpt.add(nodes_copy.get(var).get_cpt());
		}
		for(int i=0;i<hidden.size();i++)
		{
			String  temp_var=hidden.get(i);
			if(ancestor.contains(temp_var))
			{
				for(Hashtable<String,Double> cpt_table:cpt)
				{
					Set<String> key_table = cpt_table.keySet();
					for(String cpt_keys_String:key_table)
					{
						String[] val=nodes_copy.get(hidden.get(i)).get_value();
						for(String val_temp:val)
						{
							if(cpt_keys_String.contains(hidden.get(i)+":"+val_temp)&&cpt_keys_String.indexOf(hidden.get(i)+":"+val_temp)!=-1)
							{
								if(!copy_cpt.contains(cpt_table))
									copy_cpt.add(cpt_table);

							}
						}
					}
				}
				for(Hashtable<String,Double> cpt_table:copy_cpt)
				{
					cpt.remove(cpt_table);
				}
				cpt.add(pick_next_join(copy_cpt,temp_var,nodes_copy));
				copy_cpt.clear();
			}
		}

		return cpt;
	}
	/*
  what is going to be picked to join next time
	 */
	public static Hashtable<String,Double>  pick_next_join(ArrayList<Hashtable<String,Double>> copy_cpt,String hidden_var,Hashtable <String,Node>nodes_copy)
	{
		while(copy_cpt.size()>1)
		{
			HashSet<String> cpt_set=new  HashSet<String>();
			HashSet<String> cpt_set_min=new  HashSet<String>();
			int index_i=0,index_j=1,sum=-1,asci_min=-1,asci_i,asci_j;
			for(int i=0;i<copy_cpt.size()-1;i++)
			{
				String cpt_i=copy_cpt.get(i).keySet().iterator().next();
				String[] temp_i=cpt_i.split(" ");
				asci_i=0;
				for(int j=i+1;j<copy_cpt.size();j++)
				{
					String cpt_j=copy_cpt.get(j).keySet().iterator().next();
					String[] temp_j=cpt_j.split(" ");
					asci_j=0;
					for(String sub_j:temp_j)
					{
						cpt_set.add(sub_j.substring(0,sub_j.indexOf(":")));
						for(int o=0;o<sub_j.substring(0,sub_j.indexOf(":")).length();o++)
							asci_j+=(int)sub_j.substring(0,sub_j.indexOf(":")).charAt(o);
					}
					for(String sub_i:temp_i)
					{
						cpt_set.add(sub_i.substring(0,sub_i.indexOf(":")));
						if(asci_i!=0)
							for(int o=0;o<sub_i.substring(0,sub_i.indexOf(":")).length();o++)
								asci_i+=(int)sub_i.substring(0,sub_i.indexOf(":")).charAt(o);
					}
					int temp_sum=1;
					for(String vAr:cpt_set)
					{
						temp_sum*=nodes_copy.get(vAr).get_value().length;
					}
					if(sum>temp_sum||sum==-1)
					{
						index_i=i;
						index_j=j;
						sum=temp_sum;
						asci_min=asci_i+asci_j;
						cpt_set_min.clear();
						for(String  var:cpt_set )
							cpt_set_min.add(var);
					}
					else
					{
						if(sum==temp_sum&&asci_i+asci_j<asci_min)
						{
							index_i=i;
							index_j=j;
							sum=temp_sum;
							asci_min=asci_i+asci_j;
							cpt_set_min.clear();
							for(String  var:cpt_set )
								cpt_set_min.add(var);
						}
					}
					cpt_set.clear();
				}
			}
			//join cpt here
			Hashtable<String,Double> copy_i=new Hashtable<String,Double>(copy_cpt.get(index_i));
			Hashtable<String,Double> copy_j=new Hashtable<String,Double>(copy_cpt.get(index_j));
			copy_cpt.remove(Math.max(index_i,index_j));//i know i can put here j just to make sure
			copy_cpt.remove(Math.min(index_i,index_j));///i know i can put here i just to make sure
			copy_cpt.add(join_Cpt(copy_i,copy_j,nodes_copy, cpt_set_min));
		}

		Hashtable<String,Double> elimanated=new Hashtable<String,Double>(sum_join_and_eliminion(copy_cpt.get(0),hidden_var,nodes_copy.get(hidden_var).get_value()));
		return elimanated;
	}
	/*
    join CPT of and sum the value of cpt that have now same string
	 */
	public static Hashtable<String,Double> sum_join_and_eliminion(Hashtable<String,Double> copy_cpt,String hidden_var,String[] hidden_val)
	{
		Set<String> keys_copy_cpt = copy_cpt.keySet();
		Hashtable<String,Double> ret_fact=new   Hashtable<String,Double>();
		for(String temp:keys_copy_cpt)
		{
			for(String temp_val:hidden_val)
			{
				String temp_less_var=temp;
				String str_temp=temp_less_var;
				temp_less_var=temp.replace(hidden_var+":"+temp_val+" ","");
				if(str_temp.contains(hidden_var+":"+temp_val+" "))
				{
					if(ret_fact.keySet().contains(temp_less_var))
					{
						double temp_sum=ret_fact.get(temp_less_var)+copy_cpt.get(temp);
						add_counter++;
						ret_fact.remove(temp_less_var);
						ret_fact.put(temp_less_var,temp_sum);
					}
					else
					{
						ret_fact.put(temp_less_var,copy_cpt.get(temp));
					}
				}
			}
		}
		return ret_fact;
	}
	/*
  join and mult the cpt that need to
	 */
	public static Hashtable<String,Double>join_Cpt(Hashtable<String,Double> copy_i,Hashtable<String,Double>copy_j,Hashtable<String,Node>nodes_copy,  HashSet<String> cpt_set_min)
	{

		Hashtable<String,Double> comb_cpt=new  Hashtable<String,Double>();
		Set<String> keys_copy_j = copy_j.keySet();
		Set<String> keys_copy_i = copy_i.keySet();
		for(String str_i:keys_copy_i)
		{
			String[] str_arr_i=str_i.split(" ");
			for(String str_j:keys_copy_j)
			{
				String[] str_arr_j=str_j.split(" ");
				Hashtable<String,String> temp=new  Hashtable<String,String>();

				for(int i=0;i<str_arr_i.length;i++)
				{
					temp.put(str_arr_i[i].substring(0,str_arr_i[i].indexOf(":")),str_arr_i[i]);
				}
				for(int j=0;j<str_arr_j.length;j++)
				{
					if(temp.containsKey(str_arr_j[j].substring(0,str_arr_j[j].indexOf(":"))))
					{
						if(temp.get(str_arr_j[j].substring(0,str_arr_j[j].indexOf(":"))).equals(str_arr_j[j]))
						{
							continue;
						}
						else
						{
							temp.clear();
							break;
						}
					}
					else
					{
						temp.put(str_arr_j[j].substring(0,str_arr_j[j].indexOf(":")),str_arr_j[j]);
					}
				}
				if(temp.size()!=0)
				{
					List<String> val= new ArrayList<String>(temp.values());
					Collections.sort(val);
					comb_cpt.put(String.join(" ",val) + " ",copy_i.get(str_i)*copy_j.get(str_j));
					mul_counter++;

				}
			}
		}

		return comb_cpt;
	}
	/*
find all ancestor in a recursve function
	 */
	public static  HashSet<String> rec_find_ancestor(Hashtable<String, Node> nodes_copy,String str, HashSet<String> ancestor)
	{
		ancestor.add(str);
		for(String var:nodes_copy.get(str).get_parents())
		{
			ancestor.add(var);
			ancestor =  rec_find_ancestor(nodes_copy,var,ancestor);
		}
		return ancestor;
	}
	/*
  copy of algo 2 but with a diffrent way to ordered the hidden by samllest CPT size if tie by perents
	 */
	public static String algo_3(String[] queries,  ArrayList<String> hidden ,Hashtable <String,String> un_hidden,String var ,String var_value )
	{
		//fast end just like in algo_1
		Double odds=0.0;
		boolean bool=false;
		Node check=nodes.get(var);
		if(check.get_parents().size()==queries.length-1)
		{
			String str="";
			for(int i=0;i<queries.length&&!bool;i++)
			{
				if(check.get_parents().contains(queries[i].substring(0,queries[i].indexOf(":")))||queries[i].substring(0,queries[i].indexOf(":")).equals(var))
				{
					str+=queries[i];
				}
				else
					bool=true;
			}
			if(!bool)
			{
				return df.format(check.get_value(str));
			}
		}
		//eliminion
		Hashtable<String, Node> nodes_copy =new Hashtable<String, Node>(make_new_one_node(nodes));
		for(int i=0;i<queries.length;i++)
		{
			Set<String> keys = nodes.keySet();
			for(String node_var: keys)
			{
				if(!queries[i].substring(0,queries[i].indexOf(":")).equals(var))
					nodes_copy.get(node_var).eliminion(queries[i]);
			}
		}
		//delet one line CPT
		Set<String> keys = new HashSet<String> (nodes_copy.keySet());
		HashSet<String>ancestor=new HashSet<String>();
		for(int i=0;i<queries.length;i++)
		{
			String temp=queries[i].substring(0,queries[i].indexOf(":"));
			ancestor=rec_find_ancestor(nodes_copy,temp,ancestor);
		}
		//delete all values that i do not use becuse i eliminion
		for(String node_var: keys)
			if(nodes_copy.get(node_var).Cpt.size()<2 || !ancestor.contains(node_var))
			{
				nodes_copy.remove(node_var);
			}
		int  hidden_size=hidden.size();
		for(int j=0 ;j<hidden_size;j++)
		{
			if(!ancestor.contains(hidden.get(j)))
			{
				hidden.remove(hidden.get(j));
				hidden_size--;
				j--;
			}
		}
    //sort hidden by cpt size if equals by perents number
		Collections.sort(hidden, new Comparator<String>(){
			public int compare(String o1, String o2)
			{
				int sum=nodes_copy.get(o1).get_cpt().size()-nodes_copy.get(o2).get_cpt().size();
				if(sum==0)
				{
					if(nodes_copy.get(o1).get_parents().isEmpty())
						return -1;
					if(nodes_copy.get(o2).get_parents().isEmpty())
						return 1;
					sum=nodes_copy.get(o1).get_parents().size()-nodes_copy.get(o2).get_parents().size();
				}
				return sum;
			}
		});
		ArrayList<Hashtable<String,Double>> cpt=join_cpt(nodes_copy,ancestor,var_value,var,hidden);
		while(cpt.size()!=1)
		{
			HashSet<String> cpt_set=new  HashSet<String>();
			HashSet<String> cpt_set_min=new  HashSet<String>();
			int index_i=0,index_j=1,sum=-1,asci_min=-1,asci_i,asci_j;
			for(int i=0;i<cpt.size()-1;i++)
			{
				String cpt_i=cpt.get(i).keySet().iterator().next();
				String[] temp_i=cpt_i.split(" ");
				asci_i=0;
				for(int j=i+1;j<cpt.size();j++)
				{
					String cpt_j=cpt.get(j).keySet().iterator().next();
					String[] temp_j=cpt_j.split(" ");
					asci_j=0;
					for(String sub_j:temp_j)
					{
						cpt_set.add(sub_j.substring(0,sub_j.indexOf(":")));
						for(int o=0;o<sub_j.substring(0,sub_j.indexOf(":")).length();o++)
							asci_j+=(int)sub_j.substring(0,sub_j.indexOf(":")).charAt(o);
					}
					for(String sub_i:temp_i)
					{
						cpt_set.add(sub_i.substring(0,sub_i.indexOf(":")));
						if(asci_i!=0)
							for(int o=0;o<sub_i.substring(0,sub_i.indexOf(":")).length();o++)
								asci_i+=(int)sub_i.substring(0,sub_i.indexOf(":")).charAt(o);
					}
					int temp_sum=1;
					for(String vAr:cpt_set)
					{
						temp_sum*=nodes_copy.get(vAr).get_value().length;
					}
					if(sum>temp_sum||sum==-1)
					{
						index_i=i;
						index_j=j;
						sum=temp_sum;
						asci_min=asci_i+asci_j;
						cpt_set_min.clear();
						for(String  var_str_for_end:cpt_set )
							cpt_set_min.add(var_str_for_end);
					}
					else
					{
						if(sum==temp_sum&&asci_i+asci_j<asci_min)
						{
							index_i=i;
							index_j=j;
							sum=temp_sum;
							asci_min=asci_i+asci_j;
							cpt_set_min.clear();
							for(String  var_str_for_end:cpt_set )
								cpt_set_min.add(var_str_for_end);
						}
					}
					cpt_set.clear();
				}
			}
			//join cpt here
			Hashtable<String,Double> copy_i=new Hashtable<String,Double>(cpt.get(index_i));
			Hashtable<String,Double> copy_j=new Hashtable<String,Double>(cpt.get(index_j));
			cpt.remove(Math.max(index_i,index_j));//i know i can put here j just to make sure
			cpt.remove(Math.min(index_i,index_j));///i know i can put here i just to make sure
			cpt.add(join_Cpt(copy_i,copy_j,nodes_copy, cpt_set_min));
		}
		Double sum_over_all=0.0,sum_value_only=0.0;
		for(Hashtable<String,Double> nirmoul:cpt)
		{
			for(String nirmul_str:nirmoul.keySet())
			{
				if(nirmul_str.contains(var+":"+var_value+" "))
					sum_value_only+=nirmoul.get(nirmul_str);
				else
					sum_over_all+=nirmoul.get(nirmul_str);

			}
		}
		odds=sum_value_only/(sum_over_all+sum_value_only);
		add_counter++;
		double nice_odds=(double)Math.round((odds) * 100000d) / 100000d;
		return df.format(nice_odds);
	}
	/*
  make the output.txt file
	 */
	public static void make_txt()
	{
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
			out.write(text);
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception");
		}
	}
	public static void main(String[] args)
	{
		// start parsing the input.txt
		try
		{
			String var;
			String line="";
			double sum,temp_num;
			int num=0,algo=0;
			String[] Str_perent;
			//read the file
			Scanner linReader = new Scanner(new File("input.txt"));
			Hashtable<String,String> perent_map= new Hashtable<String,String>();
			//skip Network
			line=linReader.nextLine();
			//get all variavles that in the file
			line=linReader.nextLine();
			ArrayList<String> hidden = new ArrayList<String>();
			line=line.replace("Variables: ","");
			//put values in a string array
			String[] var_split=line.split(",");
			//add them to hidden
			for(int k=0;k<var_split.length;k++)
			{
				hidden.add(var_split[k]);
			}
			//skip empty line
			line=linReader.nextLine();
			line=linReader.nextLine();
			//start adding vars to a Node
			while(line.substring(0,3).equals("Var"))//Var
			{
				List<String> per= new ArrayList<String>();
				Hashtable<String, Double > map = new Hashtable<String,Double >();
				var=line.substring(4);
				line=linReader.nextLine();
				line=line.replace("Values: ","");
				String[] Str_val=line.split(",");
				line=linReader.nextLine();
				line=line.replace("Parents: ","");
				if(line.contains("none"))
				{
					line=line.replace("none","");
					perent_map.put(var,"none");
					Str_perent=line.split(",");
				}
				else
				{
					perent_map.put(var,line);
					Str_perent=line.split(",");
					for(int p=0;p<Str_perent.length;p++)
						per.add(Str_perent[p]);
				}
				line=linReader.nextLine();
				// skip CPT:
				line=linReader.nextLine();
				//run on all var
				while(!"".equals(line))
				{
					String sub_cpt="",str="";
					sum=1.0;
					sub_cpt=line.substring(0,line.indexOf("="));
					String[] sub_Cpt=sub_cpt.split(",");
					String sub_line=line.substring(line.indexOf("=")+1);
					sub_line=sub_line.replaceAll("=","");
					String[] cpt=sub_line.split(",");
					List<String> str_array =new ArrayList<String>();
					for(int h=0;h<Math.max(Str_perent.length,0);h++)
					{
						str_array.add(Str_perent[h]+":"+sub_Cpt[h]+" ");
					}
					for(int j=0;j<cpt.length;j+=2)
					{
						temp_num=Double.parseDouble( cpt[j+1]);
						if(Str_perent.length==0)
						{
							map.put(var+":"+cpt[j]+ " ",temp_num);
						}
						else {
							str_array.add(var+":"+cpt[j] + " ");
							Collections.sort(str_array);
							map.put(String.join("", str_array).replace(": ", ""),temp_num);
							str_array.remove(var+":"+cpt[j] + " ");
						}
						sum-=temp_num;
					}
					sum =(double)Math.round((sum) * 100000d) / 100000d;
					if(Str_perent.length!=0) {
						str_array.add(var+":"+Str_val[Str_val.length-1] + " ");
						Collections.sort(str_array);
						map.put(String.join("", str_array).replace(": ", ""),sum);
					}
					else
						map.put(var+":"+Str_val[Str_val.length-1] + " ",sum);
					line=linReader.nextLine();
					//where all info are saved
					nodes.put(var,new Node(var,Str_val,per,map));
				}
				line=linReader.nextLine();//skip empty line
			}
			line=linReader.nextLine();//skip Queries
			//run on all queries
			while(!"".equals(line))
			{
				ArrayList<String> hidden_copy = new ArrayList<String>(hidden);
				Hashtable<String,String> un_hidden = new Hashtable<String,String>();
				line=line.replace("|",",");
				algo=Integer.parseInt(line.substring(line.length()-1));
				line=line.substring(2,line.length()-3);
				line=line.replaceAll("=",":");
				String[] queries=line.split(",");
				String temp=queries[0];
				List<String> al = Arrays.asList(queries);
				//from https://www.geeksforgeeks.org/conversion-of-array-to-arraylist-in-java/
				Collections.sort(al);
				for(int p=0;p<al.size();p++)
				{
					queries[p]=al.get(p)+" ";
				}
				for(int p=0;p<queries.length;p++)
				{
					hidden_copy.remove(new String(queries[p].substring(0,queries[p].indexOf(":"))));
					un_hidden.put(queries[p].substring(0,queries[p].indexOf(":")),(queries[p].substring(queries[p].indexOf(":")+1)));
				}
				Collections.sort(hidden_copy);
				String find_char=temp.substring(0,temp.indexOf(":"));
				String find_value=temp.substring(temp.indexOf(":")+1);
				add_counter=0;
				mul_counter=0;
				//what algo to run
				if(algo==1)
				{
					text+=algo_1(queries,hidden_copy,un_hidden,find_char,find_value)+","+add_counter+","+mul_counter+"\n";
				}
				else if(algo==2)
				{
					text+=algo_2(queries,hidden_copy,un_hidden,find_char,find_value)+","+add_counter+","+mul_counter+"\n";
				}
				else
				{
					text+=algo_3(queries,hidden_copy,un_hidden,find_char,find_value)+","+add_counter+","+mul_counter+"\n";
				}
				if(linReader.hasNext())
					line=linReader.nextLine();//skip Queries
				else
					line="";
			}
			//make the output.txt file
			make_txt();
		}
		//if there is a prblem
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
