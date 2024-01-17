package es.um.sisdist.backend.grpc.impl;

import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.PerformMapReduceRequest;
import es.um.sisdist.backend.grpc.PerformMapReduceResponse;
import es.um.sisdist.backend.grpc.PingRequest;
import es.um.sisdist.backend.grpc.PingResponse;
import es.um.sisdist.backend.grpc.impl.jscheme.JSchemeProvider;
import es.um.sisdist.backend.grpc.impl.jscheme.MapReduceApply;
import io.grpc.stub.StreamObserver;

class GrpcServiceImpl extends GrpcServiceGrpc.GrpcServiceImplBase 
{
	private Logger logger;
	
    public GrpcServiceImpl(Logger logger) 
    {
		super();
		this.logger = logger;
		
	}

	@Override
	public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) 
	{
		logger.info("Recived PING request, value = " + request.getV());
		responseObserver.onNext(PingResponse.newBuilder().setV(request.getV()).build());
		responseObserver.onCompleted();
	}
	
	@Override
	public void mapReduce(
	    PerformMapReduceRequest request,
	    StreamObserver<PerformMapReduceResponse> responseObserver) {
	    // acceder a los parámetros de la solicitud, como la base de datos, la función, etc.
		logger.info("Recived MAP request, value = " + request.toString());
	   
		MapReduceApply mapreduce = new MapReduceApply(JSchemeProvider.js(),
				"(import \"java.lang.String\")"
				+ "(define (ssdd-map k v)"
				+ " (emit (list k (string->number v))))",
				"(define (ssdd-reduce k l)" +
				" (apply + l))");
	    LinkedList<String> pares = new LinkedList<String>();
	    // Dividir la cadena en pares clave,valor usando la coma como separador
        String[] paresArray = request.getMapreduce().split(", ");

        for (String par : paresArray) {
            pares.add(par);
        }
      
	    // Realiza procesamiento para cada par de clave-valor de la lista
	 
	    for (String par : pares) {
            String[] keyValue = par.split(":");
            String key = keyValue[0];
            String value = keyValue[1];
            mapreduce.apply(key,value); // aplicamos map reduce a cada par
	    }
	    
	    Map<Object, Object> resultado = mapreduce.map_reduce(); // map reduce
	    try {
			logger.info("Thread Sleep"); // el hilo debe parar por procesamiento anterior
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    //  generar la respuesta y envía el resultado al cliente
	    PerformMapReduceResponse response = PerformMapReduceResponse.newBuilder()
	        .setMapreduce(resultado.toString())
	        .build();
	    responseObserver.onNext(response);
	    responseObserver.onCompleted();// Terminar la respuesta.
	}
	

/*
	@Override
	public void storeImage(ImageData request, StreamObserver<Empty> responseObserver)
    {
		logger.info("Add image " + request.getId());
    	imageMap.put(request.getId(),request);
    	responseObserver.onNext(Empty.newBuilder().build());
    	responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<ImageData> storeImages(StreamObserver<Empty> responseObserver) 
	{
		// La respuesta, sólo un objeto Empty
		responseObserver.onNext(Empty.newBuilder().build());

		// Se retorna un objeto que, al ser llamado en onNext() con cada
		// elemento enviado por el cliente, reacciona correctamente
		return new StreamObserver<ImageData>() {
			@Override
			public void onCompleted() {
				// Terminar la respuesta.
				responseObserver.onCompleted();
			}
			@Override
			public void onError(Throwable arg0) {
			}
			@Override
			public void onNext(ImageData imagedata) 
			{
				logger.info("Add image (multiple) " + imagedata.getId());
		    	imageMap.put(imagedata.getId(), imagedata);	
			}
		};
	}

	@Override
	public void obtainImage(ImageSpec request, StreamObserver<ImageData> responseObserver) {
		// TODO Auto-generated method stub
		super.obtainImage(request, responseObserver);
	}

	@Override
	public StreamObserver<ImageSpec> obtainCollage(StreamObserver<ImageData> responseObserver) {
		// TODO Auto-generated method stub
		return super.obtainCollage(responseObserver);
	}
	*/
}