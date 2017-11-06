package it.unibz.inf.ontop.protege.panels;

/*
 * #%L
 * ontop-protege4
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import it.unibz.inf.ontop.protege.gui.IconLoader;
import it.unibz.inf.ontop.protege.gui.treemodels.QueryControllerTreeModel;
import it.unibz.inf.ontop.protege.gui.treemodels.QueryGroupTreeElement;
import it.unibz.inf.ontop.protege.gui.treemodels.QueryTreeElement;
import it.unibz.inf.ontop.protege.gui.treemodels.TreeElement;
import it.unibz.inf.ontop.protege.utils.DialogUtils;
import it.unibz.inf.ontop.utils.querymanager.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Dialog.ModalityType;
import java.util.Vector;

/**
 * This class represents the display of stored queries using a tree structure.
 */
public class SavedQueriesPanel extends JPanel implements QueryControllerListener {

	private static final long serialVersionUID = 6920100822784727963L;
	
	private Vector<SavedQueriesPanelListener> listeners = new Vector<SavedQueriesPanelListener>();
	
	private QueryControllerTreeModel queryControllerModel = new QueryControllerTreeModel();

	private QueryController queryController;
		
	private QueryTreeElement currentId;
	private QueryTreeElement previousId;

	/** 
	 * Creates new form SavedQueriesPanel 
	 */
	public SavedQueriesPanel(QueryController queryController) {
		
		initComponents();		

		this.queryController = queryController;
		this.queryController.addListener(queryControllerModel);
		this.queryController.addListener(this);
		
		// Fill the tree model with existing elements from the controller
		queryControllerModel.synchronize(queryController.getElements());
		queryControllerModel.reload();
	}

	public void changeQueryController(QueryController newQueryController) {
		// Reset and load the current tree model
		queryControllerModel.reset();
		queryControllerModel.synchronize(queryController.getElements());
		queryControllerModel.reload();

		if (queryController != null) {
			queryController.removeAllListeners();
		}
		queryController = newQueryController;
		queryController.addListener(queryControllerModel);
		queryController.addListener(this);
	}

	public void addQueryManagerListener(SavedQueriesPanelListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void removeQueryManagerListener(SavedQueriesPanelListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlSavedQuery = new javax.swing.JPanel();
        scrSavedQuery = new javax.swing.JScrollPane();
        treSavedQuery = new javax.swing.JTree();
        pnlCommandPanel = new javax.swing.JPanel();
        lblSavedQuery = new javax.swing.JLabel();
        cmdRemove = new javax.swing.JButton();
        cmdAdd = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pnlSavedQuery.setMinimumSize(new java.awt.Dimension(200, 50));
        pnlSavedQuery.setLayout(new java.awt.BorderLayout());

        scrSavedQuery.setMinimumSize(new java.awt.Dimension(400, 200));
        scrSavedQuery.setOpaque(false);
        scrSavedQuery.setPreferredSize(new java.awt.Dimension(300, 200));

        treSavedQuery.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        treSavedQuery.setForeground(new java.awt.Color(51, 51, 51));
        treSavedQuery.setModel(queryControllerModel);
        treSavedQuery.setCellRenderer(new SavedQueriesTreeCellRenderer());
        treSavedQuery.setMaximumSize(new java.awt.Dimension(5000, 5000));
        treSavedQuery.setRootVisible(false);
        treSavedQuery.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reselectQueryNode(evt);
            }
        });
        treSavedQuery.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                selectQueryNode(evt);
            }
        });
        scrSavedQuery.setViewportView(treSavedQuery);

        pnlSavedQuery.add(scrSavedQuery, java.awt.BorderLayout.CENTER);

        pnlCommandPanel.setLayout(new java.awt.GridBagLayout());

        lblSavedQuery.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        lblSavedQuery.setForeground(new java.awt.Color(153, 153, 153));
        lblSavedQuery.setText("Stored Query:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.5;
        pnlCommandPanel.add(lblSavedQuery, gridBagConstraints);

        cmdRemove.setIcon(IconLoader.getImageIcon("images/minus.png"));
        cmdRemove.setText("Remove");
        cmdRemove.setToolTipText("Remove the selected query");
        cmdRemove.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdRemove.setContentAreaFilled(false);
        cmdRemove.setIconTextGap(5);
        cmdRemove.setMaximumSize(new java.awt.Dimension(25, 25));
        cmdRemove.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdRemove.setPreferredSize(new java.awt.Dimension(80, 25));
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlCommandPanel.add(cmdRemove, gridBagConstraints);

        cmdAdd.setIcon(IconLoader.getImageIcon("images/plus.png"));
        cmdAdd.setText("Add");
        cmdAdd.setToolTipText("Add a new query");
        cmdAdd.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdAdd.setContentAreaFilled(false);
        cmdAdd.setIconTextGap(4);
        cmdAdd.setMaximumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setMinimumSize(new java.awt.Dimension(25, 25));
        cmdAdd.setPreferredSize(new java.awt.Dimension(63, 25));
        cmdAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        pnlCommandPanel.add(cmdAdd, gridBagConstraints);

        pnlSavedQuery.add(pnlCommandPanel, java.awt.BorderLayout.NORTH);

        add(pnlSavedQuery, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    private void selectQueryNode(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_selectQueryNode
    	String currentQuery = "";
    	
    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
        if (node instanceof QueryTreeElement) {
            QueryTreeElement queryElement = (QueryTreeElement)node;
            currentQuery = queryElement.getQuery();
            currentId = queryElement;
            
            TreeNode parent = queryElement.getParent();
            if (parent == null || parent.toString().equals("")) {
                fireQueryChanged("", currentQuery, currentId.getID());
            }
            else {
                fireQueryChanged(parent.toString(), currentQuery, currentId.getID());
            }
        }
        else if (node instanceof QueryGroupTreeElement) {
            QueryGroupTreeElement groupElement = (QueryGroupTreeElement)node;
            currentId = null;
            currentQuery = null;
            fireQueryChanged(groupElement.toString(), "", "");
        }
        else if (node == null) {
            currentId = null;
            currentQuery = null;
        }
    }//GEN-LAST:event_selectQueryNode

	private void reselectQueryNode(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reselectQueryNode
		if (currentId == null) {
			return;
		}
		if (previousId == currentId) {
			fireQueryChanged(currentId.getParent().toString(), currentId.getQuery(), currentId.getID());
		}
		else { // register the selected node
			previousId = currentId;
		}
	}//GEN-LAST:event_reselectQueryNode

    private void cmdAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAddActionPerformed
    	// Open a save dialog if it is a new query
		JDialog saveDialog = new JDialog();
		saveDialog.setTitle("New Query");
		saveDialog.setModalityType(ModalityType.MODELESS);
		
		SaveQueryPanel savePanel = new SaveQueryPanel("", saveDialog, queryController);
		saveDialog.getContentPane().add(savePanel, java.awt.BorderLayout.CENTER);
		saveDialog.pack();
		
		DialogUtils.centerDialogWRTParent(this, saveDialog);
		DialogUtils.installEscapeCloseOperation(saveDialog);
		
		saveDialog.setVisible(true);
    }//GEN-LAST:event_cmdAddActionPerformed

	private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeQueryButtonActionPerformed
		TreePath selected_path = treSavedQuery.getSelectionPath();
		if (selected_path == null)
			return;

		if (JOptionPane.showConfirmDialog(this, "This will delete the selected query. \n Continue? ", "Delete confirmation",
				JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.CANCEL_OPTION) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selected_path.getLastPathComponent();
		if (node instanceof TreeElement) {
			TreeElement element = (TreeElement) node;
			QueryController qc = this.queryController;
			if (node instanceof QueryTreeElement) {
				qc.removeQuery(element.getID());
			} else if (node instanceof QueryGroupTreeElement) {
				qc.removeGroup(element.getID());
			}
		}
		
		
	}// GEN-LAST:event_removeQueryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAdd;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JLabel lblSavedQuery;
    private javax.swing.JPanel pnlCommandPanel;
    private javax.swing.JPanel pnlSavedQuery;
    private javax.swing.JScrollPane scrSavedQuery;
    private javax.swing.JTree treSavedQuery;
    // End of variables declaration//GEN-END:variables

	public void fireQueryChanged(String newgroup, String newquery, String newid) {
		for (SavedQueriesPanelListener listener : listeners) {
			listener.selectedQueryChanged(newgroup, newquery, newid);
		}
	}

	@Override
	public void elementAdded(QueryControllerEntity element) {
		String elementId = element.getID();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) queryControllerModel.getNode(elementId);

		// Select the new node in the JTree
		treSavedQuery.setSelectionPath(new TreePath(node.getPath()));
		treSavedQuery.scrollPathToVisible(new TreePath(node.getPath()));
	}

	@Override
	public void elementAdded(QueryControllerQuery query, QueryControllerGroup group) {
		String queryId = query.getID();
		String groupId = group.getID();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) queryControllerModel.getElementQuery(queryId, groupId);
				
		// Select the new node in the JTree
		treSavedQuery.setSelectionPath(new TreePath(node.getPath()));
		treSavedQuery.scrollPathToVisible(new TreePath(node.getPath()));
	}

	@Override
	public void elementRemoved(QueryControllerEntity element) {
		fireQueryChanged("", "", "");
	}

	@Override
	public void elementRemoved(QueryControllerQuery query, QueryControllerGroup group) {
		fireQueryChanged("", "", "");
	}

	@Override
	public void elementChanged(QueryControllerQuery query, QueryControllerGroup group) {
		String queryId = query.getID();
		String groupId = group.getID();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) queryControllerModel.getElementQuery(queryId, groupId);
				
		// Select the modified node in the JTree
		treSavedQuery.setSelectionPath(new TreePath(node.getPath()));
		treSavedQuery.scrollPathToVisible(new TreePath(node.getPath()));
	}

	@Override
	public void elementChanged(QueryControllerQuery query) {
		String queryId = query.getID();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) queryControllerModel.getNode(queryId);
		
		// Select the modified node in the JTree
		treSavedQuery.setSelectionPath(new TreePath(node.getPath()));
		treSavedQuery.scrollPathToVisible(new TreePath(node.getPath()));
	}
}
