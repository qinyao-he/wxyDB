package me.hqythu.wxydb.sql;

public class DoubleReturn<T1,T2>
{
	T1 first;
	T2 second;
	public DoubleReturn()
	{
		first = null;
		second = null;
	}
	public DoubleReturn(T1 f, T2 s)
	{
		first = f;
		second = s;
	}
}
