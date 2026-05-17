declare var FlowChart: {

  setContainer(id: string): void;

  addNode(position: any, elId: any): void;

  undo(): void;

  init(): void;

  zoomIn(): void;

  zoomOut(): void;

  loadData(data: any): void;

  getData(): any;

  setData(data: any): any;

  getNodeDataByNodeId(id: any): any;

  execModel(): Promise<undefined>;

  on(eventName: string, fn: Function): void;
}

export default FlowChart