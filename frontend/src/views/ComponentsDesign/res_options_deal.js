const resOptionsDeal = {
  getFieldFormats: function (row, options, filterField) {
    let newOptions = [];
    options.forEach((option) => {
      if (option.name === row[filterField]) {
        newOptions = option.formate;
      }
    })
    return newOptions.map(item => {
      return {
        name: item
      }
    });
  },
  getStepFields: function (row, optionsConfig) {

  }
}

export default resOptionsDeal;
