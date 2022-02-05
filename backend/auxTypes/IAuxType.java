package backend.auxTypes;

import backend.eval.CircuitEvaluator;
import backend.structure.Wire;
import java.math.BigInteger;

public interface IAuxType {
  Wire[] toWires();
  
  void mapValue(BigInteger paramBigInteger, CircuitEvaluator paramCircuitEvaluator);
  
  void mapRandomValue(CircuitEvaluator paramCircuitEvaluator);
  
  BigInteger getValueFromEvaluator(CircuitEvaluator paramCircuitEvaluator);
  
  void makeOutput(String... paramVarArgs);
  
  Bit[] getBitElements();
  
  void forceEqual(IAuxType paramIAuxType);
  
  Bit isEqualTo(IAuxType paramIAuxType);
  
  Bit isNotEqualTo(IAuxType paramIAuxType);
  
  IAuxType copy();
  
  VariableState getState();
  
  PackedValue getPackedValue();
  
  boolean isConstant();
  
  BigInteger getConstant();
  
  int getRequiredBitWidth();
  
  int getCurrentBitWidth();
  
  void verifyRange();
}


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\IAuxType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */