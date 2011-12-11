package com.matjazmuhic.tree;

import com.matjazmuhic.util.Dimensions;

public interface IBlockNode extends ITreeNode
{
	public ITreeNode[] getChildren();
	public void addChild(BlockNode node, int position);
	public void removeChild(int position);
	public boolean hasChildren();
	public Dimensions getDimensions();
	public String getGeometryId(); 

}
