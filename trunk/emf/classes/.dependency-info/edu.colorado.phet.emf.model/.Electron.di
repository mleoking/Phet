       JC:\PhET\emf\development\classes\edu\colorado\phet\emf\model\Electron.class $edu.colorado.phet.emf.model.Electron +edu.colorado.phet.common.model.ModelElement Electron.java   !        model &Ledu/colorado/phet/emf/model/EmfModel;    startPosition Ljava/awt/geom/Point2D;    prevPosition Ljava/awt/geom/Point2D;    currentPosition Ljava/awt/geom/Point2D;    velocity (Ledu/colorado/phet/common/math/Vector2D;    movementStrategy 3Ledu/colorado/phet/emf/model/movement/MovementType;    runningTime D    staticFieldStrength (Ledu/colorado/phet/common/math/Vector2D;    steps I    positionHistory [Ljava/awt/geom/Point2D;    accelerationHistory )[Ledu/colorado/phet/common/math/Vector2D;    maxAccelerationHistory )[Ledu/colorado/phet/common/math/Vector2D;    movementStrategyHistory 4[Ledu/colorado/phet/emf/model/movement/MovementType;    
changeFreq Z    newFreq F    changeAmplitude Z    newAmplitude F    recordHistory Z    dynamicFieldStrength (Ledu/colorado/phet/common/math/Vector2D;    fieldStrength (Ledu/colorado/phet/common/math/Vector2D;    s_retardedFieldLength I     � s_B F   Dz   s_staticFieldScale F   BH   
s_restMass F   ?�   
s_stepSize I   
    <init> G(Ledu/colorado/phet/emf/model/EmfModel;Ljava/awt/geom/Point2D$Double;)V        getCurrentPosition ()Ljava/awt/geom/Point2D;        setCurrentPosition (Ljava/awt/geom/Point2D;)V   !     setMovementStrategy 6(Ledu/colorado/phet/emf/model/movement/MovementType;)V        setRecordHistory (Z)V        
stepInTime (D)V   !     moveToNewPosition (Ljava/awt/geom/Point2D;)V   !     recordPosition (Ljava/awt/geom/Point2D;)V        getVelocity *()Ledu/colorado/phet/common/math/Vector2D;        getStartPosition ()Ljava/awt/geom/Point2D;        getInstantaneousStaticField A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;        getInstantaneousDynamicField A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;        getFieldAtLocation A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;        getDynamicFieldAt A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;        getStaticFieldAt A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;        getAccelerationAt (I)F        getPositionAt (I)F        getPositionAt (Ljava/awt/geom/Point2D;)F        getMovementTypeAt L(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/emf/model/movement/MovementType;        getMass ()F        setFrequency (F)V        setAmplitude (F)V        getMaxAccelerationAtLocation H(Ljava/awt/geom/Point2D$Double;)Ledu/colorado/phet/common/math/Vector2D;        getMaxAccelerationAtLocation :(Ljava/awt/Point;)Ledu/colorado/phet/common/math/Vector2D;        
isFieldOff (D)Z        getRestMass ()F   	     getMovementStrategyType ()Ljava/lang/Class;           5edu.colorado.phet.emf.view.TransmitterElectronGraphic     7edu.colorado.phet.emf.model.PositionConstrainedElectron    <init> G(Ledu/colorado/phet/emf/model/EmfModel;Ljava/awt/geom/Point2D$Double;)V         setCurrentPosition (Ljava/awt/geom/Point2D;)V         *edu.colorado.phet.emf.model.ElectronSpring    getCurrentPosition ()Ljava/awt/geom/Point2D;         getMass ()F         getVelocity *()Ledu/colorado/phet/common/math/Vector2D;         setCurrentPosition (Ljava/awt/geom/Point2D;)V         updateObservers ()V         3edu.colorado.phet.emf.model.movement.ManualMovement    setCurrentPosition (Ljava/awt/geom/Point2D;)V         getCurrentPosition ()Ljava/awt/geom/Point2D;         -edu.colorado.phet.emf.view.StripChartDelegate    addObserver (Ljava/util/Observer;)V         getCurrentPosition ()Ljava/awt/geom/Point2D;         *edu.colorado.phet.emf.view.ElectronGraphic    getCurrentPosition ()Ljava/awt/geom/Point2D;         moveToNewPosition (Ljava/awt/geom/Point2D;)V         +edu.colorado.phet.emf.view.FieldLatticeView    addObserver (Ljava/util/Observer;)V         getCurrentPosition ()Ljava/awt/geom/Point2D;         getDynamicFieldAt A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;         getStaticFieldAt A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;         getStartPosition ()Ljava/awt/geom/Point2D;         7edu.colorado.phet.emf.model.movement.SinusoidalMovement    getStartPosition ()Ljava/awt/geom/Point2D;         setCurrentPosition (Ljava/awt/geom/Point2D;)V         edu.colorado.phet.emf.EmfModule    getStartPosition ()Ljava/awt/geom/Point2D;         getStartPosition ()Ljava/awt/geom/Point2D;         getStartPosition ()Ljava/awt/geom/Point2D;         getPositionAt (I)F         getPositionAt (I)F         getPositionAt (I)F         getPositionAt (I)F         moveToNewPosition (Ljava/awt/geom/Point2D;)V         #edu.colorado.phet.emf.view.EmfPanel     4edu.colorado.phet.command.AddTransmittingElectronCmd     3edu.colorado.phet.emf.view.ReceivingElectronGraphic     .edu.colorado.phet.emf.model.EmfSensingElectron    setMovementStrategy 6(Ledu/colorado/phet/emf/model/movement/MovementType;)V         setRecordHistory (Z)V         
stepInTime (D)V         getVelocity *()Ledu/colorado/phet/common/math/Vector2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         
isFieldOff (D)Z         getStartPosition ()Ljava/awt/geom/Point2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getMovementTypeAt L(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/emf/model/movement/MovementType;         getPositionAt (Ljava/awt/geom/Point2D;)F         getStartPosition ()Ljava/awt/geom/Point2D;         getStartPosition ()Ljava/awt/geom/Point2D;         getDynamicFieldAt A(Ljava/awt/geom/Point2D;)Ledu/colorado/phet/common/math/Vector2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getCurrentPosition ()Ljava/awt/geom/Point2D;         getStartPosition ()Ljava/awt/geom/Point2D;         setMovementStrategy 6(Ledu/colorado/phet/emf/model/movement/MovementType;)V         $edu.colorado.phet.emf.model.EmfModel    setMovementStrategy 6(Ledu/colorado/phet/emf/model/movement/MovementType;)V         setFrequency (F)V         setAmplitude (F)V         1edu.colorado.phet.emf.model.movement.MovementType     ?edu.colorado.phet.emf.model.movement.RelativisticManualMovement    getMass ()F            java.lang.Class      $edu.colorado.phet.emf.EmfApplication      java.awt.geom.Point2D$Double      +edu.colorado.phet.common.model.ModelElement      java.awt.geom.Point2D      java.lang.Object      java.lang.Math      3edu.colorado.phet.emf.model.movement.ManualMovement      java.io.PrintStream      &edu.colorado.phet.common.math.Vector2D      java.awt.Point      7edu.colorado.phet.emf.model.movement.SinusoidalMovement      java.lang.System      java.awt.geom.Point2D$Float      $edu.colorado.phet.emf.model.EmfModel      1edu.colorado.phet.emf.model.movement.MovementType     