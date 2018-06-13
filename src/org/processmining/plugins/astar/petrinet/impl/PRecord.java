package org.processmining.plugins.astar.petrinet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import gnu.trove.TIntCollection;
import gnu.trove.set.TShortSet;
import nl.tue.astar.AStarThread;
import nl.tue.astar.Delegate;
import nl.tue.astar.Head;
import nl.tue.astar.Record;
import nl.tue.astar.Tail;
import nl.tue.astar.Trace;
import nl.tue.astar.impl.State;
import nl.tue.storage.CompressedStore;
import nl.tue.storage.StorageException;
import nl.tue.storage.compressor.BitMask;

public class PRecord implements Record {

	//                           header: 24 bytes 
	protected long state; //                8 bytes
	protected double estimate; //           8 bytes
	protected final int cost; //         8 bytes
	protected final PRecord predecessor; // 8 bytes
	protected final int logMove; //         4 bytes //��trace�е�activity��Ӧ���±�
	protected final int modelMove; //       4 bytes 
	protected final int backtrace; //       4 bytes
	protected final BitMask executed;
	protected boolean exact;

	//                            total: 70 -> 72 bytes. 

	public PRecord(long state, int cost, PRecord predecessor, int logMove, int modelMove, int markingsize,
			int backtrace, BitMask executed) {
		this.state = state;
		this.cost = cost;
		this.predecessor = predecessor;
		this.logMove = logMove;
		this.modelMove = modelMove;
		this.backtrace = backtrace;
		this.executed = executed;
	}

	public PRecord(int cost, PRecord predecessor, int markingsize, int traceLength) {
		this.cost = cost;
		this.predecessor = predecessor;
		this.logMove = AStarThread.NOMOVE;
		this.modelMove = AStarThread.NOMOVE;
		this.backtrace = -1;
		this.executed = new BitMask(traceLength);
	}

	public <H extends Head, T extends Tail> State<H, T> getState(CompressedStore<State<H, T>> storage)
			throws StorageException {
		return storage.getObject(state);
	}

	public long getState() {
		return state;
	}

	public int getCostSoFar() {
		return cost;
	}

	public PRecord getPredecessor() {
		return predecessor;
	}

	public double getTotalCost() {
		return cost + estimate;
	}

	public void setState(long index) {
		this.state = index;
	}

	/**
	 * In case of a LogMove only, then logMove>=0, modelMove ==
	 * AStarThread.NOMOVE,
	 * 
	 * In case of a ModelMove only, then logMove == AStarThread.NOMOVE,
	 * modelMove >=0,
	 * 
	 * in case of both log and model move, then logMove>=0, modelMove>=0,
	 * 
	 */
	public PRecord getNextRecord(Delegate<? extends Head, ? extends Tail> d, Trace trace, Head nextHead, long state,
			int modelMove, int movedEvent, int activity) {
		AbstractPDelegate<? extends Tail> delegate = (AbstractPDelegate<?>) d;
		assert !(modelMove != AStarThread.NOMOVE && movedEvent != AStarThread.NOMOVE)
				|| delegate.getActivitiesFor((short) modelMove).contains((short) activity);

		//added by weizhijie
//		System.out.println("*");
//		activity=movedEvent;
//		if(activity>=0)
//		System.out.println(trace.get(movedEvent)+","+activity);
		int c = -1;
		int flag = -1;
		if (modelMove == AStarThread.NOMOVE)
			flag = 0;//LogMove only
		else if (movedEvent == AStarThread.NOMOVE)
			flag = 1;//ModelMove only
		else
			flag = 2;//both
		//		System.out.println(delegate.getEpsilon()+"#"+delegate.getDelta());
		if (flag == 1) {//skip
			Transition tCu = delegate.int2trans.get((short) modelMove);
			if (tCu.isInvisible()) {
				c = delegate.getEpsilon();
			} else {
				String cu = tCu.getLabel();
				if (cu.equals("e")) {
					c = (int) ((1000 * delegate.getDelta()) + delegate.getEpsilon());
				} else {
					Set<String> targetPre = new HashSet<String>();
					Set<String> cuAllPre = delegate.computeCost.getPre().get(cu);
					PRecord pre = this.predecessor;
					while (pre != null) {
						if (pre.modelMove >= 0) {//skip or syn
							Transition t = delegate.int2trans.get((short) pre.modelMove);
							if (cuAllPre.contains(t.getLabel()))
								targetPre.add(t.getLabel());
						}
						pre = pre.predecessor;
					}
//					if(trace.getLabel().equals("3ADAA1EE7B01BE1B4B3AFB14C88D96AB")&&cu.equals("9")){
//						System.out.println(targetPre+","+cu+","+delegate.computeCost.costOfTargetEventAfterPre(targetPre, cu) * delegate.getDelta());
//					}
					c = (int) (delegate.computeCost.costOfTargetEventAfterPre(targetPre, cu) * delegate.getDelta()
							+ delegate.getEpsilon());
				}
			}
		} else if (flag == 0) {//insert
			if (delegate.int2act.get((short) activity).getId().equals("e"))
				c = (int) (1000 * delegate.getDelta() + delegate.getEpsilon());
			else {
				PRecord pre = this.predecessor;
				while (pre != null) {
					if (pre.modelMove >= 0 && !delegate.int2trans.get((short) pre.modelMove).isInvisible())
//					if ((pre.modelMove >= 0 && pre.logMove >= 0 && !delegate.int2trans.get((short) pre.modelMove).isInvisible())||
//							(pre.modelMove==AStarThread.NOMOVE&&pre.logMove>=0))
						break;
					pre = pre.predecessor;
				}
				if (pre != null) {
					String tar = delegate.int2trans.get((short) pre.modelMove).getLabel();
					String cu = delegate.int2act.get((short) activity).getId();
//					if(trace.getLabel().equals("3ADAA1EE7B01BE1B4B3AFB14C88D96AB")&&cu.equals("0")){
//						System.out.println(tar+","+cu);
//						Stack<PRecord> stack=new Stack<PRecord>();
//						PRecord it = this.predecessor;
//						while (it != null) {
//							if ((it.modelMove >= 0 && !delegate.int2trans.get((short) it.modelMove).isInvisible())||
//									(it.logMove >= 0)){
//								stack.push(it);
//							}
//							it = it.predecessor;
//						}
//						while(!stack.empty()){
//							PRecord p=stack.pop();
//							if(p.logMove>=0&&p.modelMove>=0){
//								System.out.print("("+delegate.int2trans.get((short) p.modelMove).getLabel()+","+"f)->");
//							}else if(p.logMove>=0&&p.modelMove==AStarThread.NOMOVE){
//								System.out.print("("+delegate.int2act.get((short) p.logMove).getId()+","+"i)->");
//							}else{
//								System.out.print("("+delegate.int2trans.get((short) p.modelMove).getLabel()+","+"s)->");
//							}
//						}
//						System.out.println();
//					}
//					if(trace.getLabel().equals("3ADAA1EE7B01BE1B4B3AFB14C88D96AB")&&cu.equals("0")){
//						System.out.println(tar+","+cu+","+delegate.computeCost.costOfTargetEventAfterPre(tar, cu) * delegate.getDelta());
//					}
					c = (int) (delegate.computeCost.costOfTargetEventAfterPre(tar, cu) * delegate.getDelta()
							+ delegate.getEpsilon());
				} else {
					c = (int) (delegate.getDelta() * 1 + delegate.getEpsilon());
				}
			}
		} else {
			c = delegate.getEpsilon();
		}
//		System.out.println(c);
		//		c = delegate.getCostFor(modelMove, activity);

		//		if (modelMove >= 0) {//bu insert
		//			Transition tCu = delegate.int2trans.get((short) modelMove);
		//			if (!tCu.isInvisible()) {
		//				String cu = tCu.getLabel();
		//
		//				PRecord pre = this.predecessor;
		//				while (pre != null && pre.modelMove >= 0
		//						&& delegate.int2trans.get((short) pre.modelMove).isInvisible()) {//���Բ��ɼ�
		//					pre = pre.predecessor;
		//				}
		//				if (pre != null && pre.logMove >= 0 && pre.modelMove == AStarThread.NOMOVE) {
		//					int insertCnt = 1;
		//					pre = pre.predecessor;
		//					while (pre != null) {
		//						if (pre.logMove >= 0 && pre.modelMove == AStarThread.NOMOVE)
		//							insertCnt++;
		//						else if(delegate.int2trans.get((short) pre.modelMove).isInvisible()){
		//							;
		//						}else
		//							break;
		//						pre = pre.predecessor;
		//					}
		//					Set<String> targetPre = new HashSet<String>();
		//					Set<String> cuAllPre = delegate.computeCost.getPre().get(cu);
		//					while (pre != null) {
		//						if (pre.modelMove >= 0) {//skip or syn
		//							Transition t = delegate.int2trans.get((short) pre.modelMove);
		//							if (cuAllPre.contains(t.getLabel()))
		//								targetPre.add(t.getLabel());
		//						}
		//						pre = pre.predecessor;
		//					}
		//					int temp = (int) ((delegate.computeCost.costOfTargetEventAfterPre(targetPre, cu) - 1)
		//							* delegate.getDelta() + delegate.getEpsilon());
		//					c += temp * insertCnt;
		//				}
		//			}
		//			
		//		}
		//		System.out.println(c);

		//				PRecord pre=this.predecessor;
		//				while(true){
		//					if(pre==null) break;
		//					if(flag==0){//�����ǰ��logmove��������һ�������Ҫ��ǰ�ҵ�һ��logmove��synmove����modelmove���ɼ��ڵ��modelmove��
		//						if(pre.logMove>=0||delegate.int2trans.get((short)pre.modelMove).isInvisible()==false)
		//							break;
		//					}else if(flag==1){//�����ǰ��modelmove��������һ�������Ҫ��ǰ��һ��modelmove
		//						if((pre.logMove>=0&&pre.modelMove>=0)||
		//								(pre.logMove==AStarThread.NOMOVE&&pre.modelMove>=0&&delegate.int2trans.get((short)pre.modelMove).isInvisible()==false))
		//							break;
		//					}else
		//						break;
		//					pre=pre.predecessor;
		//				}
		//				c=-1;
		//		//		for(Map.Entry<Short, Integer> entry:delegate.act2cost.en )
		//		//		System.out.println(delegate.act2cost);
		//				if((flag==1&&delegate.int2trans.get((short)modelMove).isInvisible())||flag==2||pre==null)
		//					c = delegate.getCostFor(modelMove, activity);
		//				else{
		//		//			if(pre.logMove>=16)
		//		//			System.out.println(flag+":"+pre.logMove+":"+pre.modelMove);
		//					int temp=-1;
		//					if(pre.logMove==AStarThread.NOMOVE)
		//						temp=AStarThread.NOMOVE;
		//					else
		//						temp=trace.get(pre.logMove);
		//					c = delegate.getCostFor(modelMove, activity,pre.modelMove,temp,delegate);
		//				}

		BitMask newExecuted;
		if (movedEvent != AStarThread.NOMOVE) {
			newExecuted = executed.clone();
			newExecuted.set(movedEvent, true);
			//			newExecuted = Arrays.copyOf(executed, executed.length);
			//			newExecuted[movedEvent] = true;
		} else {
			newExecuted = executed;
		}

		PRecord r = new PRecord(state, cost + c, this, movedEvent, modelMove,
				((PHead) nextHead).getMarking().getNumElts(), backtrace + 1, newExecuted);
		//		System.out.println("____________________________");
		////				System.out.println(r.getCostSoFar());
		//				int insertCnt=0;
		//				int sipCnt=0;
		//				int synCnt=0;
		////				if(r.getCostSoFar()>1000000){
		////					System.out.println("************************");
		//					PRecord test=r;
		//					PRecord it=test;
		////					System.out.println("total:"+it.getTotalCost());
		//					while(it.getPredecessor()!=null){
		////						System.out.println(it.getLogMove()+","+it.getModelMove()+","+(it.getCostSoFar()-it.getPredecessor().getCostSoFar()));
		//						if (it.modelMove == AStarThread.NOMOVE)
		//							insertCnt ++;//LogMove only
		//						else if (it.logMove == AStarThread.NOMOVE)
		//							sipCnt++;//ModelMove only
		//						else
		//							synCnt++ ;//both
		//						it=it.getPredecessor();
		//					}
		//					if (it.modelMove == AStarThread.NOMOVE)
		//						insertCnt ++;//LogMove only
		//					else if (it.logMove == AStarThread.NOMOVE)
		//						sipCnt++;//ModelMove only
		//					else
		//						synCnt++ ;//both
		//					if(trace.getLabel().equals("752B4032D9EC0A485B272CCA1462DAE2")&&synCnt>=8){
		//						System.out.println(insertCnt+","+sipCnt);
		//						System.out.println("**************************");
		//					}
		////					System.out.println(it.getLogMove()+","+it.getModelMove()+","+it.getCostSoFar());
		////					System.out.println("______________________________");
		////				}
		////				r.setEstimatedRemainingCost(0, false);
		return r;
	}

	public int getLogMove() {
		return logMove;
	}

	public double getEstimatedRemainingCost() {
		return estimate;
	}

	public void setEstimatedRemainingCost(double cost, boolean isExactEstimate) {
		//assert isExactEstimate;
		this.estimate = cost;
		this.exact = isExactEstimate;

	}

	public boolean equals(Object o) {
		return (o instanceof Record) && ((Record) o).getState() == state;
	}

	public int hashCode() {
		return (int) state;
	}

	public String toString() {
		return "[s:" + state + " c:" + cost + " e:" + estimate + "]";
	}

	public int getModelMove() {
		return modelMove;
	}

	public static <P extends PRecord> List<P> getHistory(P r) {
		if (r == null || r.getBacktraceSize() < 0) {
			return Collections.emptyList();
		}
		List<P> history = new ArrayList<P>(r.getBacktraceSize() + 1);
		while (r.getPredecessor() != null) {
			history.add(0, r);
			r = (P) r.getPredecessor();
		}
		return history;
	}

	public static void printRecord(AbstractPDelegate<?> delegate, int trace, PRecord r) {
		List<PRecord> history = getHistory(r);

		for (int i = 0; i < history.size(); i++) {
			r = history.get(i);
			String s = "(";
			int act = delegate.getActivityOf(trace, r.getMovedEvent());

			if (r.getModelMove() == AStarThread.NOMOVE) {
				s += "_";
			} else {
				short m = (short) r.getModelMove();
				s += "(" + m + ")";
				// t is either a transition in the model, or AStarThread.NOMOVE
				TShortSet acts = delegate.getActivitiesFor(m);
				if (act == AStarThread.NOMOVE || acts == null || acts.isEmpty() || !acts.contains((short) act)) {
					s += delegate.getTransition(m);
				} else {
					s += delegate.getEventClass((short) act);
				}
			}

			s += ",";
			// r.getLogEvent() is the event that was moved, or AStarThread.NOMOVE
			if (r.getMovedEvent() == AStarThread.NOMOVE) {
				s += "_";
			} else {
				assert (act >= 0 || act < 0);
				s += "(" + r.getMovedEvent() + ")" + delegate.getEventClass((short) act);
			}
			s += ") " + r.toString();
			s += (i < history.size() - 1 ? " --> " : " cost: " + (r.getCostSoFar()));
			System.out.print(s);
		}
		System.out.println();
	}

	public int getMovedEvent() {
		return logMove;
	}

	public TIntCollection getNextEvents(Delegate<? extends Head, ? extends Tail> delegate, Trace trace) {
		return trace.getNextEvents(executed);
	}

	public int getBacktraceSize() {
		return backtrace;
	}

	public boolean isExactEstimate() {
		return exact;
	}

}
